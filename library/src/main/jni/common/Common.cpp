//
/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-12 13:16
 * Description:  
 */
//

/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: Common.java
 * Create: 2016/1/12 13:16
 */
#include "Common.h"
//构造函数
Common::Common() {
}
//虚构函数
Common::~Common() {
}

int Common::createSocket(int port,void (*callback)()){
    UdpCore udpcore;
    udpcore.startBroadCastServer(port,callback);
}

/**
 * Get the process name according to pid.
 */
char* Common::get_name_by_pid(pid_t pid) {
    char proc_file_path[BUFFER_SIZE];
    char buffer[BUFFER_SIZE];
    char *process_name;

    process_name = (char *) malloc(BUFFER_SIZE);
    if (!process_name)
    {
        return NULL;
    }

    sprintf(proc_file_path, "/proc/%d/cmdline", pid);
    FILE *fp = fopen(proc_file_path, "r");
    if (fp != NULL)
    {
        if (fgets(buffer, BUFFER_SIZE - 1, fp) != NULL)
        {
            fclose(fp);

            sscanf(buffer, "%[^-]", process_name);
            return process_name;
        }
    }

    return NULL;
}

/**
 * Get the version of current SDK.
 */
int Common::get_version() {
    char value[8] = "";
    __system_property_get("ro.build.version.sdk", value);
    return atoi(value);
}

/**
 * Find pid by process name.
 */
int Common::find_pid_by_name(char *pid_name, int *pid_list) {
    DIR *dir;
    struct dirent *next;
    int i = 0;
    pid_list[0] = 0;

    dir = opendir("/proc");
    if (!dir)
    {
        return 0;
    }

    while ((next = readdir(dir)) != NULL)
    {
        FILE *status;
        char proc_file_name[BUFFER_SIZE];
        char buffer[BUFFER_SIZE];
        char process_name[BUFFER_SIZE];

        /* skip ".." */
        if (strcmp(next->d_name, "..") == 0)
        {
            continue;
        }

        /* pid dir in proc is number */
        if (!isdigit(*next->d_name))
        {
            continue;
        }

        sprintf(proc_file_name, "/proc/%s/cmdline", next->d_name);
        if (!(status = fopen(proc_file_name, "r")))
        {
            continue;
        }

        if (fgets(buffer, BUFFER_SIZE - 1, status) == NULL)
        {
            fclose(status);
            continue;
        }
        fclose(status);

        /* get pid list */
        sscanf(buffer, "%[^-]", process_name);
        if (strcmp(process_name, pid_name) == 0)
        {
            pid_list[i ++] = atoi(next->d_name);
        }
    }

    if (pid_list)
    {
        pid_list[i] = 0;
    }

    closedir(dir);

    return i;
}

/* open browser with specified url */
void Common::open_browser(char *url) {
    /* the url cannot be null */
    if (url == NULL || strlen(url) < 4) {
        return;
    }

    /* get the sdk version */
    char value[8] = "";
    __system_property_get("ro.build.version.sdk", value);

    int version = atoi(value);
    /* is the version is greater than 17 */
    if (version >= 17 || version == 0) {
        execlp("am", "am", "start", "--user", "0", "-n",
               "com.android.browser/com.android.browser.BrowserActivity",
               "-a", "android.intent.action.VIEW",
               "-d", url, (char *) NULL);
    }
    else {
        execlp("am", "am", "start", "-n",
               "com.android.browser/com.android.browser.BrowserActivity",
               "-a", "android.intent.action.VIEW",
               "-d", url, (char *) NULL);
    }
}

/**
 * Use `select` to sleep with specidied second and microsecond.
 */
void Common::select_sleep(long sec, long msec) {
    struct timeval timeout;

    timeout.tv_sec = sec;
    timeout.tv_usec = msec * 1000;

    select(0, NULL, NULL, NULL, &timeout);
}

/**
 * stitch two string to one string
 *
 * @param  str1 the first string to be stitched
 * @param  str2 the second string to be stitched
 * @return      stitched string
 */
char* Common::str_stitching(const char *str1, const char *str2) {
    char *result;
    result = (char *) malloc(strlen(str1) + strlen(str2) + 1);
    if (!result)
    {
        return NULL;
    }

    strcpy(result, str1);
    strcat(result, str2);

    return result;
}


/**
 * check if the process is running. by using ps command.
 * @param  processName [description]
 * @return             [description]
 */
int Common::isProcessExist(char* processName) {
    char buf[BUFFER_SIZE];
    char command[BUFFER_SIZE];
    FILE* fp;
    int ret = 0;
    sprintf(command, "ps -c | grep %s", processName);

    //log2file("command is: %s\n", command);

    if ((fp = popen(command, "r")) == NULL) {
        Logc("popen failed\n");
        exit(1);
    }

    if ((fgets(buf, BUFFER_SIZE, fp)) != NULL) {
        ret = 1;
//        Logc("ps info:%s\n", buf);
    } else {
        Logc("process: %s not running.ret:%d\n", processName,ret);
    }

    pclose(fp);
    return ret;
}

/* start daemon service */
void Common::start_service(char *package_name, char *service_name)
{
    /* get the sdk version */
    int version = get_version();
    pid_t pid = fork();
//    Logc("get the fork pid:%d",pid);
    if (pid < 0)
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

        char *p_name = str_stitching(package_name, "/");
        char *s_name = str_stitching(p_name, service_name);
//        Logc("service: %s", s_name);

        int ret = -1000;
        if (version >= 17 || version == 0)
        {
            execlp("am", "am", "startservice",
                   "--user", "0", "-n", s_name, (char *) NULL);
            Logc("result %d", ret);
        }
        else
        {//startservice
            ret = execlp("am", "am", "startservice", "-n", s_name, (char *) NULL);
        }

        Logc("exit start-service child process ret:%d",ret);
        exit(EXIT_SUCCESS);
    }
    else
    {
        Logc("waitpid,pid is:%d",pid);
        waitpid(pid, NULL, 0);
    }
}

/**
 * actully run the android app/service
 * @param packageName [description]
 * @param serviceName [description]
 */
void Common::runProcess(char* packageName, char* serviceName) {
    FILE* fp;
    char service_name[BUFFER_SIZE];
    sprintf(service_name, "%s/%s", packageName, serviceName);

    char command[BUFFER_SIZE];
    sprintf(command, "am startservice --user 0 -n %s", service_name);
    // sprintf(command, "am startservice -n %s/%s", packageName, serviceName);
    Logc("run cmd: %s\n", command);


    //execlp("am","am","startservice","--user",uid,"-n",service_name, (char *)NULL);

    if ((fp = popen(command, "r")) == NULL) {
        Logc("popen failed\n");
        exit(1);
    }
    pclose(fp);
}