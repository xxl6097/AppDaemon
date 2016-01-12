//
/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-12 13:45
 * Description:  
 */
//

/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife 
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: Daemon.java
 * Create: 2016/1/12 13:45
 */
#include <sys/stat.h>
#include <sys/wait.h>
#include <signal.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/prctl.h>

#include "Common.h"

#define	MAXFILE         3
#define SLEEP_INTERVAL  1
volatile int sig_running = 1;
static void sigterm_handler(int signo);
static Common common;


int main(int argc, char *argv[])
{
    int i;
    pid_t pid;
    char *package_name = NULL;
    char *service_name = NULL;
    char *daemon_file_dir = NULL;
    int interval = SLEEP_INTERVAL;
    char *processName = NULL;
    int tmp = 0;
    Logc("Copyright (c) 2015 clife, Shenzhen H&T Intelligent Control Co.,Ltd. argc.len:%d",argc);

    if (argc < 7)
    {
        Logc("usage: %s -p package-name -s "
                "daemon-service-name -t interval-time", argv[0]);
        for (i = 0; i < argc; i ++) {
            if (!strcmp("-y", argv[i])) {
                tmp = atoi(argv[i + 1]);
                if (tmp == 1) {
                    Logc("receive app exit order....%d", tmp);
                    exit(EXIT_SUCCESS);
                }
            }
        }
        return 0;
    }

    for (i = 0; i < argc; i ++)
    {
        if (!strcmp("-p", argv[i]))
        {
            package_name = argv[i + 1];
        }

        if (!strcmp("-s", argv[i]))
        {
            service_name = argv[i + 1];
        }

        if (!strcmp("-t", argv[i]))
        {
            interval = atoi(argv[i + 1]);
        }

        if (!strcmp("-z", argv[i]))
        {
            processName = argv[i + 1];
        }

        if (!strcmp("-y", argv[i]))
        {
            tmp = atoi(argv[i + 1]);
        }
    }

    Logc("package name: %s , service name: %s , interval: %d, processName:%s", package_name,service_name,interval,processName);
    /* package name and service name should not be null */
    if (package_name == NULL || service_name == NULL)
    {
        Logc("package name or service name is null");
        return 0;
    }
    pid = fork();
    Logc("the init fork pid is:%d",pid);
    if (pid < 0)
    {
        Logc("the init fork pid(%d) < 0, so exit",pid);
        exit(EXIT_SUCCESS);
    }
    else if (pid == 0)
    {
        /* add signal */
        signal(SIGTERM, sigterm_handler);//kill 命令发出 的信号

//        signal(SIGINT, sigterm_handler);//来自键盘的中断信号 ( ctrl + c ) .
//        signal(SIGHUP, sigterm_handler);//从终端上发出的结束信号.
//        signal(SIGQUIT, exit_handler);
//        signal(SIGPIPE, sigterm_handler);
//        signal(SIGCHLD, sigterm_handler);
//        signal(SIGTTOU, sigterm_handler);
//        signal(SIGTTIN, sigterm_handler);
//        signal(SIGKILL, exit_handler);//该信号结束接收信号的进程 .

        /* become session leader */
        setsid();
        /* change work directory */
        chdir("/");

        for (i = 0; i < MAXFILE; i ++)
        {
            close(i);
        }

        /* find pid by name and kill them */
        int pid_list[100];
        int total_num = common.find_pid_by_name(argv[0], pid_list);
        Logc("find_pid_by_name total num %d", total_num);
        for (i = 0; i < total_num; i ++)
        {
            int retval = 0;
            int daemon_pid = pid_list[i];
            if (daemon_pid > 1 && daemon_pid != getpid())
            {
                retval = kill(daemon_pid, SIGTERM);
                if (!retval)
                {
                    Logc("kill daemon process success: %d", daemon_pid);
                }
                else
                {
                    Logc("kill daemon process %d fail: %s", daemon_pid, strerror(errno));
                    exit(EXIT_SUCCESS);
                }
            }
        }

        Logc("child process fork ok, daemon pid: %d", getpid());

        while(sig_running)
        {
            interval = interval < SLEEP_INTERVAL ? SLEEP_INTERVAL : interval;
            common.select_sleep(interval, 0);

//            Logc("check the service once, interval: %d", interval);

            if (!common.isProcessExist(processName)) {
                /* start service */
                //start_service
                common.runProcess(package_name, service_name);
            }
        }

        exit(EXIT_SUCCESS);
    }
    else
    {
        /* parent process */
        exit(EXIT_SUCCESS);
    }
}


/* signal term handler */
static void sigterm_handler(int signo)
{
    Logc("handle signal: %d ", signo);
    sig_running = 0;
}