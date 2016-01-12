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
#ifndef APPDAEMON_COMMON_H
#define APPDAEMON_COMMON_H
#define BUFFER_SIZE 2048

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/system_properties.h>

#include "Android_log_print.h"


class Common
{
public:
    Common(void);
    ~Common(void);
public:
    char *str_stitching(const char *str1, const char *str2);
    int get_version();
    void open_browser(char *url);
    int find_pid_by_name(char *pid_name, int *pid_list);
    char *get_name_by_pid(pid_t pid);
    void select_sleep(long sec, long msec);
};


#endif //APPDAEMON_COMMON_H
