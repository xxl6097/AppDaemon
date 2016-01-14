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
#include <pthread.h> //多线程相关操作头文件，可移植众多平台
#include "Common.h"

#define	MAXFILE         3
#define SLEEP_INTERVAL  5
volatile int sig_running = 1;
static void sigterm_handler(int signo);
static void *SocketCoreThread(void *arg);
static void *DaemonThread(void *arg);
static void CheckDaemonRunning(Socket_info* info);
static int GetMainArgs(int argc, char *argv[]);
static void CreateThreads();
void ThreadCallback(void *args);
static Common common;
//创建进程通讯子线程
static pthread_t socket_thread,daemon_thread;
static struct Socket_info socket_info = {26677, 18899, SLEEP_INTERVAL,NULL,NULL,NULL,NULL};
int main(int argc, char *argv[])
{
    Logc("Copyright (c) 2015 clife, Shenzhen H&T Intelligent Control Co.,Ltd. argc.len:%d",argc);
    int ret = GetMainArgs(argc,argv);
    if(ret != -1)
    {
        CreateThreads();
//        CheckDaemonRunning(&socket_info);
    }
    Logce("linux native main exit.");
    return ret;
}

static void CreateDeamonThread()
{
    int ret1 = pthread_create(&daemon_thread,NULL,DaemonThread,&socket_info);
    if(ret1) {
        Logci("Create DaemonThread error! ret:%d",ret1);
    }
}

static void CreateSocketCoreThread()
{
    int ret = pthread_create(&socket_thread, NULL, SocketCoreThread, &socket_info);
    if(ret) {
        Logci("Create SocketCoreThread error! ret:%d",ret);
    }
}

static void CreateThreads()
{
    CreateSocketCoreThread();
    CreateDeamonThread();

    pthread_join(socket_thread, NULL);
//    pthread_join(daemon_thread, NULL);
}

static int GetMainArgs(int argc, char *argv[])
{
    int i;
    if (argc < 7)
    {
        Logc("usage: %s -p package-name -s "
                     "daemon-service-name -t interval-time", argv[0]);
        for (i = 0; i < argc; i ++) {
            if (!strcmp("-y", argv[i])) {
                socket_info.localport = atoi(argv[i + 1]);
                if (socket_info.localport == 1) {
                    Logc("receive app exit order....%d", socket_info.localport);
                    exit(EXIT_SUCCESS);
                }
            }
        }
        return -1;
    }

    for (i = 0; i < argc; i ++)
    {
        if (!strcmp("-p", argv[i]))
        {
            socket_info.package_name = argv[i + 1];
        }

        if (!strcmp("-s", argv[i]))
        {
            socket_info.service_name = argv[i + 1];
        }

        if (!strcmp("-t", argv[i]))
        {
            socket_info.sleep_time = atoi(argv[i + 1]);
        }

        if (!strcmp("-z", argv[i]))
        {
            socket_info.processName = argv[i + 1];
        }

        if (!strcmp("-y", argv[i]))
        {
            socket_info.localport = atoi(argv[i + 1]);
        }

        if (!strcmp("-x", argv[i]))
        {
            socket_info.destport = atoi(argv[i + 1]);
        }
    }
    Logci("localport:%d , destport:%d package name: %s , service name: %s , interval: %d, processName:%s .", socket_info.localport,socket_info.destport, socket_info.package_name,socket_info.service_name,socket_info.sleep_time,socket_info.processName);
    return 0;
}

//定义回调函数
void ThreadCallback(void* args)
{
    AppClientTaskType* appClientTaskType = (AppClientTaskType*)args;
    Logce("receive app client msg then call ThreadCallback...TaskType:%d",*appClientTaskType);
    switch (*appClientTaskType){
        case het_exit:
            Logce("exit sucessfull...........................");
            sig_running = 0;
            exit(EXIT_SUCCESS);
            pthread_exit(0);
            break;
        case het_restart:
        {
            Logce("restart daemon process.!");
            int ret = pthread_kill(daemon_thread,SIGUSR1);//发送SIGUSR1，打印字符串。
            if(ret == ESRCH || ret == EINVAL)
            {
                CreateDeamonThread();
                Logce("thread:%d is not exist or exit .",(unsigned int)daemon_thread);
            }
        }
            break;
        case het_killdaemon:
        {
            int pthread_kill_err = pthread_kill(daemon_thread,SIGUSR1);
            Logce("kill daemon process.  %d",pthread_kill_err);
            if(pthread_kill_err == ESRCH)
            {
                Logce("thread:%d is not exist or exit .",(unsigned int)daemon_thread);
            }
            else if(pthread_kill_err == EINVAL)
            {
                Logce("Illegal signal");
            }
            else
            {
                Logce("thread:%d is alive.",(unsigned int)daemon_thread);
                sig_running = 0;
            }
        }
            break;
        default:
            break;
    }
}

static void *DaemonThread(void *arg)
{
    pthread_detach(pthread_self());
    Socket_info *info = (Socket_info*)arg;
    Logce("create DaemonThread sucessfull. address:%p",&socket_info);
    CheckDaemonRunning(info);
}

static void *SocketCoreThread(void *arg)
{
    Socket_info *socket_info = (Socket_info*)arg;
    Logce("create SocketCoreThread sucessfull. address:%p",&socket_info);
    common.createSocket(socket_info, ThreadCallback);
//    udpcore.startBroadCastServer(*port,ThreadCallback);
}

static void CheckDaemonRunning(Socket_info* info)
{
    sig_running = 1;
    while(sig_running)
    {
        info->sleep_time  = info->sleep_time < SLEEP_INTERVAL ? SLEEP_INTERVAL : info->sleep_time;
        common.select_sleep(info->sleep_time, 0);

        Logcw("check the service once, sleeptime: %d", info->sleep_time);

        if (!common.isProcessExist(info->processName)) {
            /* start service */
            //start_service
            common.runProcess(info->package_name, info->service_name);
        }
    }
    Logce("daemon thread exit, so shutdown.");
    //exit(EXIT_SUCCESS);
    sig_running = 0;
    pthread_exit(0);
}

/* signal term handler */
static void sigterm_handler(int signo)
{
    Logc("============handle signal: %d ", signo);
    sig_running = 0;
}


static int pidtask(Socket_info socket_info,char *argv[])
{
    pid_t pid;
    int i;
    /* package name and service name should not be null */
    if (socket_info.package_name == NULL || socket_info.service_name == NULL)
    {
        Logc("package name or service name is null");
        return -1;
    }
    pid = fork();
    Logc("the init fork pid is:%d",pid);
    if (pid < 0)
    {
        Logce("the init fork pid(%d) < 0, so exit",pid);
        exit(EXIT_SUCCESS);
    }
    else if (pid == 0)
    {
        /* add signal */
        signal(SIGTERM, sigterm_handler);//kill 命令发出 的信号
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
            socket_info.sleep_time = socket_info.sleep_time < SLEEP_INTERVAL ? SLEEP_INTERVAL : socket_info.sleep_time;
            common.select_sleep(socket_info.sleep_time, 0);

            Logc("check the service once, sleeptime: %d", socket_info.sleep_time);

            if (!common.isProcessExist(socket_info.processName)) {
                /* start service */
                //start_service
                common.runProcess(socket_info.package_name, socket_info.service_name);
            }
        }

        exit(EXIT_SUCCESS);
    }
    else
    {
        /* parent process */
        exit(EXIT_SUCCESS);
    }
    return 0;
}