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

#include "Common.h"

#define	MAXFILE         3
#define SLEEP_INTERVAL  2 * 60

volatile int sig_running = 1;
static void sigterm_handler(int signo);
static void start_service(char *package_name, char *service_name);

static Common common;


int main(int argc, char *argv[])
{
    int i;
    pid_t pid;
    char *package_name = NULL;
    char *service_name = NULL;
    char *daemon_file_dir = NULL;
    int interval = SLEEP_INTERVAL;
    Logc("Copyright (c) 2015 clife, Shenzhen H&T Intelligent Control Co.,Ltd.");

    if (argc < 7)
    {
        Logc("usage: %s -p package-name -s "
                "daemon-service-name -t interval-time", argv[0]);
        return 0;
    }

    for (i = 0; i < argc; i ++)
    {
        if (!strcmp("-p", argv[i]))
        {
            package_name = argv[i + 1];
            Logc("package name: %s", package_name);
        }

        if (!strcmp("-s", argv[i]))
        {
            service_name = argv[i + 1];
            Logc("service name: %s", service_name);
        }

        if (!strcmp("-t", argv[i]))
        {
            interval = atoi(argv[i + 1]);
            Logc( "interval: %d", interval);
        }
    }

    /* package name and service name should not be null */
    if (package_name == NULL || service_name == NULL)
    {
        Logc("package name or service name is null");
        return 0;
    }

    if ((pid = fork()) < 0)
    {
        exit(EXIT_SUCCESS);
    }
    else if (pid == 0)
    {
        /* add signal */
        signal(SIGTERM, sigterm_handler);

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
        Logc("total num %d", total_num);
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

        Logc("child process fork ok, daemon start: %d", getpid());

        while(sig_running)
        {
            interval = 10;//interval < SLEEP_INTERVAL ? SLEEP_INTERVAL : interval;
            common.select_sleep(interval, 0);

            Logc("check the service once, interval: %d", interval);

            /* start service */
            start_service(package_name, service_name);
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

/* start daemon service */
static void start_service(char *package_name, char *service_name)
{
    /* get the sdk version */
    int version = common.get_version();
    Logc("get the sdk version:%d",version);
    pid_t pid;

    if ((pid = fork()) < 0)
    {
        Logc("app exit,pid is:%d",pid);
        exit(EXIT_SUCCESS);
    }
    else if (pid == 0)
    {
        if (package_name == NULL || service_name == NULL)
        {
            Logc("package name or service name is null");
            return;
        }

        char *p_name = common.str_stitching(package_name, "/");
        char *s_name = common.str_stitching(p_name, service_name);
        Logc("service: %s", s_name);

        if (version >= 17 || version == 0)
        {
            int ret = execlp("am", "am", "startservice",
                             "--user", "0", "-n", s_name, (char *) NULL);
            Logc("result %d", ret);
        }
        else
        {
            execlp("am", "am", "startservice", "-n", s_name, (char *) NULL);
        }

        Logc("exit start-service child process");
        exit(EXIT_SUCCESS);
    }
    else
    {
        waitpid(pid, NULL, 0);
    }
}