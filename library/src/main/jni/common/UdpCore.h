// UdpCore.h: interface for the UdpCore class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_UDPCORE_H__FA9C132E_D47D_49C2_A8A5_417D7ED9F675__INCLUDED_)
#define AFX_UDPCORE_H__FA9C132E_D47D_49C2_A8A5_417D7ED9F675__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include <stdio.h>
#include <stdarg.h>
//errno
#include <errno.h>
#include <string.h>

#include <sys/types.h>
#include <sys/socket.h>

//sockaddr_un
#include <sys/un.h>

//htons,sockaddr_in
#include <netinet/in.h>
//inet_ntop
#include <arpa/inet.h>
//close,unlink
#include <unistd.h>
//offsetof
#include <stddef.h>
//gethostbyname
#include <netdb.h>


#include "commondata.h"
#include "Common.h"
//#include <android/log.h>

//#include "Android_log_print.h"

#include "Android_log_print.h"
#define GROUP_ADDR "224.0.0.2"

//#define BUFFER_SIZE 1024 * 8
enum SOCKETOPT
{
	MULTICAST,BROADCAST
};
class UdpCore  
{

public:
	UdpCore(void);
	virtual ~UdpCore(void);
private:
	//����Socketʵ�����
	int m_sockfd;
	//ͨѶ�˿ں�
	unsigned short m_port; 
	//�󶨱��ص�ַ��
	struct sockaddr_in local_addr;
	//Զ�̵�ַ��
	struct sockaddr_in remote_addr;

	//�鲥���ַ
	char* m_group_addr;
	//�󶨵�ַ
	char* m_bindIp;
	struct ip_mreq m_mreq;
public:
	AppClientTaskType GetTaskType(char* recv);
	int createSocket();
	void setPort(unsigned short port);
	void startBroadCastServer(Socket_info *socket_info,void (*callback)(void*));

	bool Bind();
	int sendTo(const char* ip,const short port,const char* buf,int len);
	int recvFrom(char** ip,int* port,char* buf,int len);
	int release();
public:
	void setGroupAddr(const char* groupAddr);
	void setBindIp(char* bindIp);
	char* getGroupAddr();
public:
	int setSockOpt(SOCKETOPT socketopt);
	int getSockOpt();
	int getSockfd();
	void initSockAddrIn(void);
	/*
public:
	int setReuseAddress(bool reuse);
	int setLoopBackMode(bool loop);
	*/
};

#endif // !defined(AFX_UDPCORE_H__FA9C132E_D47D_49C2_A8A5_417D7ED9F675__INCLUDED_)
