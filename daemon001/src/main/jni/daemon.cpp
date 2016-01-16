//
/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-15 11:19
 * Description:  
 */
//

/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife 
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: daemon.java
 * Create: 2016/1/15 11:19
 */
#include <errno.h>
#include <fcntl.h>
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/file.h>
#include <unistd.h>
#include <time.h>
#include <stdarg.h>
#include <signal.h>
#include <sys/stat.h>
#include <sys/param.h>

#include "Android_log_print.h"
#define BUFFER_LENGTH 1024
#define FLAG_FILE "daemon.lock"
#define ROOT_PATH "/data/data/"
#define SLEEP_INTERVEL 2  // every x seconds to check if process is running
#define VERSION "v100"

int isProcessExist(char* processName);
void runProcess(char* packageName, char* serviceName);
int isAppUninstalled(char* packagePath);
void log2file(const char* format, ...);
void initDaemon();

int isEnableLog = 0;
char packagePath[256] = ROOT_PATH;
char logFilePath[512] = "";

/**
 * main
 * @param  argc [6 parameters]
 * @param  argv
 * [packagename,processname,ServiceNameOrActivityName,isEnableLog,logFilePath]
 * e.g. packagename: "com.yourapp";
 * processname:"com.yourapp:servicetag" ;
 * servicename: "com.yourapp.someService";
 * isEnableLog: "1";
 * logFilePath: "";
 * @return      [description]
 */
//#define	NOFILE         3

void init_daemon(void)
{
    int pid;
    int i;
    if(pid=fork())
        exit(0);        //是父进程，结束父进程
    else if(pid< 0)
        exit(1);        //fork失败，退出
    //是第一子进程，后台继续执行
    setsid();           //第一子进程成为新的会话组长和进程组长
    //并与控制终端分离
    if(pid=fork())
        exit(0);        //是第一子进程，结束第一子进程
    else if(pid< 0)
        exit(1);        //fork失败，退出
    //是第二子进程，继续
    //第二子进程不再是会话组长
    for(i=0;i< getdtablesize();++i)  //关闭打开的文件描述符
    {
        close(i);
    }
//
    chdir("/tmp");      //改变工作目录到/tmp
    umask(0);           //重设文件创建掩模
    return;
}
int main(int argc, char** argv) {
    if (argc < 6) {
        fprintf(stderr,
                "Usage:%s <packagename> <processname> <ServiceNameOrActivityName> "
                        "<isEnableLog> <logFilePath>\n",
                argv[0]);
        return -1;
    }

    Logce("enter main...");
//    initDaemon();
    init_daemon();

    char* packageName = argv[1];
    char* processName = argv[2];
    char* serviceName = argv[3];
    char* enableLogFlag = argv[4];
    char* logFilePara = argv[5];

    strcat(logFilePath, logFilePara);

    if (strcmp(enableLogFlag, "1") == 0) {
        isEnableLog = 1;
    } else {
        isEnableLog = 0;
    }


    // packagePath = ROOT_PATH;
    // packagePath ==> /data/data/ctrip.android.view:pushsdk.v1
    strcat(packagePath, packageName);
    Logce("packagename is: %s.\nprocessname is: %s.\nservicename is: "
                  "%s\nisEnableLog: %s\npackagePath is: %s\nlogFilePath is: %s\n",
          packageName, processName, serviceName, enableLogFlag, packagePath, logFilePath);


    // check if the app has been uninstalled or the path is invalid
    if (isAppUninstalled(packagePath) == -1) {
        Logce("app has been uninstalled. exit. %s\n",packagePath);
        exit(-1);
    }

    char flagFilePath[256] = "/data/data/uuxia.het.com.sample/app_bin/daemon.lock";
    // flagFilePath ==> /data/data/ctrip.android.view:pushsdk.v1
//    strcat(flagFilePath, packagePath);
//    // flagFilePath ==> /data/data/ctrip.android.view:pushsdk.v1/
//    strcat(flagFilePath, "/");
//    // flagFilePath ==> /data/data/ctrip.android.view:pushsdk.v1/daemon.lock
//    strcat(flagFilePath, FLAG_FILE);
    Logce("flagFilePath is: %s\n", flagFilePath);

    int ret = -1;
    FILE* g_lockfile = NULL;

    //check if self is already running
    g_lockfile = fopen(flagFilePath, "a+");
    if (g_lockfile == NULL) {
        fprintf(stderr, "fopen() failed:%s!\n", strerror(errno));
        Logce( "fopen() failed:%s!\n", strerror(errno));
        return -1;
    }

    Logce("g_lockfile opened.\n");

    ret = 0;
    ret = flock(fileno(g_lockfile), LOCK_EX | LOCK_NB);
    if (ret != 0) {
        fprintf(stderr, "flock() failed:%s!\n", strerror(errno));
        Logce("daemon already running. exit.\n");
        return -1;
    }

    // check android app/service is alive
    while (1) {
        if (isAppUninstalled(packagePath) == -1) {
            Logce("app has been uninstalled. exit.\n");
            exit(-1);
        }
        if (!isProcessExist(processName)) {
            runProcess(packageName, serviceName);
        }

        Logce("sleep for %d seconds...\n************************\n", SLEEP_INTERVEL);
        sleep(SLEEP_INTERVEL);
    }

    return 0;
}

/**
 * init the daemon, all the magic in it.
 */
void initDaemon(){

    if(fork() != 0){
        exit(0);
    }

    //setpgrp();// is equivalent to setpgid(0,0)
    setsid(); //its critical

    signal(SIGINT, SIG_IGN);
    signal(SIGHUP, SIG_IGN);
    signal(SIGQUIT, SIG_IGN);
    signal(SIGPIPE, SIG_IGN);
    signal(SIGCHLD, SIG_IGN);
    signal(SIGTTOU, SIG_IGN);
    signal(SIGTTIN, SIG_IGN);
    signal(SIGTERM, SIG_IGN);
    signal(SIGKILL, SIG_IGN);

    if(fork() != 0){
        exit(0);
    }
    //chdir("/");
    //umask(0);
}

/**
 * check if app's already been uninstalled
 * @param  packagePath [description]
 * @return             [description]
 */
int isAppUninstalled(char* packagePath) {
    if (access(packagePath, 0) == -1) {
        printf("app uninstalled. file:'%s' not exits.\n", packagePath);
        return -1;
    }
    return 1;
}

/**
 * check if the process is running. by using ps command.
 * @param  processName [description]
 * @return             [description]
 */
int isProcessExist(char* processName) {
    char buf[BUFFER_LENGTH];
    char command[BUFFER_LENGTH];
    FILE* fp;
    int ret = 0;
    sprintf(command, "ps -c | grep %s", processName);

    //Logce("command is: %s\n", command);

    if ((fp = popen(command, "r")) == NULL) {
        Logc("popen failed\n");
        exit(1);
    }

    if ((fgets(buf, BUFFER_LENGTH, fp)) != NULL) {
        ret = 1;
        Logc("ps info:%s\n", buf);
    } else {
        Logc("process: %s not running.\n", processName);
    }

    pclose(fp);
    return ret;
}

/**
 * actully run the android app/service
 * @param packageName [description]
 * @param serviceName [description]
 */
void runProcess(char* packageName, char* serviceName) {
    FILE* fp;
    char service_name[BUFFER_LENGTH];
    sprintf(service_name, "%s/%s", packageName, serviceName);

    char command[BUFFER_LENGTH];
    sprintf(command, "am startservice --user 0 -n %s", service_name);
    // sprintf(command, "am startservice -n %s/%s", packageName, serviceName);
    Logce("run cmd: %s\n", command);


    //execlp("am","am","startservice","--user",uid,"-n",service_name, (char *)NULL);

    if ((fp = popen(command, "r")) == NULL) {
        Logce("popen failed\n");
        exit(1);
    }

    pclose(fp);
}

/**
 * log to file
 */
void log2file(const char* format, ...) {

    if (!isEnableLog) {
        return;
    }

    char logContent[BUFFER_LENGTH] = "";
    va_list args;
    va_start(args, format);
    vsprintf(logContent, format, args);
    va_end(args);

    //use date as the log file name
    time_t currentTime = time(NULL);
    char logFileName[20] = "";
    strftime(logFileName, sizeof(logFileName), "%F",
             localtime(&currentTime));  // yyyy-mm-dd
    char logTime[20] = "";
    strftime(logTime, sizeof(logTime), "%T",
             localtime(&currentTime));  // H%:M%:S%

    char logContentWithTimestamp[BUFFER_LENGTH] = "";
    sprintf(logContentWithTimestamp, "%s : %s", logTime, logContent);
    printf("%s\n", logContentWithTimestamp);

    char logFileFullName[256] = "";
    //printf("log file path is: %s/%s.log\n", packagePath, logFileName);
    sprintf(logFileFullName, "%s/daemon-%s-%s.log", logFilePath, VERSION, logFileName);

    FILE* logfile = fopen(logFileFullName, "a+");
    if (logfile == NULL) {
        //fprintf(stderr, "fopen() failed:%s!\n", strerror(errno));
        logfile = fopen(logFileFullName, "w+");
    }
    if (logfile == NULL) {
        fprintf(stderr, "fopen() failed:%s!\n", strerror(errno));
        return;
    }
    fprintf(logfile, "%s\n", logContentWithTimestamp);
    fclose(logfile);
}


