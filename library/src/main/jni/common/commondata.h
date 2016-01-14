//
/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-14 14:58
 * Description:  
 */
//

/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: commondata.java
 * Create: 2016/1/14 14:58
 */
#ifndef APPDAEMON_COMMONDATA_H
#define APPDAEMON_COMMONDATA_H
struct Socket_info{
    int localport;
    int destport;
    int sleep_time;
    char *processName;
    char *package_name;
    char *service_name;
    char *daemon_file_dir;
} ;
enum AppClientTaskType
{
    het_exit,het_killdaemon,het_restart,het_error
};

//struct Callback_info{
//    AppClientTaskType taskType;
//};
#endif //APPDAEMON_COMMONDATA_H
