// UdpCore.cpp: implementation of the UdpCore class.
//
//////////////////////////////////////////////////////////////////////
#include "UdpCore.h"

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
// API说明：http://blog.csdn.net/maopig/article/details/17193021
//////////////////////////////////////////////////////////////////////

AppClientTaskType UdpCore::GetTaskType(char *recvmsg)
{
	if(strcmp(recvmsg, "exit") == 0){
		return het_exit;
	}
	if(strcmp(recvmsg, "restart") == 0){
		return het_restart;
	}
	if(strcmp(recvmsg, "killdaemon") == 0){
		return het_killdaemon;
	}
	return het_error;
}

void UdpCore::startBroadCastServer(Socket_info *socket_info,void (*callback)(void* args)) {
	setvbuf(stdout, NULL, _IONBF, 0);
	fflush(stdout);
	int sock = -1;
	char recvmsg[BUFFER_SIZE] = {""};
	sock = createSocket();
	if (sock > 0)
	{
		setPort(socket_info->localport);
		int nb = setSockOpt(BROADCAST);
		if (nb == 0)
		{
			if (Bind())
			{
				while (true)
				{
					char* ip;// = "255.255.255.255";
					int nPort;
					memset(recvmsg,0,sizeof(recvmsg));
					int ret = recvFrom(&ip,&nPort,recvmsg,BUFFER_SIZE);
					if(ret > 0){
						Logci("uulog c++ native:%s:%d size:%d,data=%s\n",ip,nPort,ret,recvmsg);
						char sendmsg[BUFFER_SIZE] = {"msg come from linux native:"};
						strcat(sendmsg,recvmsg);
						sendTo(ip, socket_info->destport, sendmsg, BUFFER_SIZE);
						AppClientTaskType result = GetTaskType(recvmsg);//strcmp(recvmsg, "exit");
						callback(&result);
//						if (result == exit) {
//							Callback_info info;
//							callback(NULL);
//							Logce("call a callback to exit daemon process.\n");
//						}
					}else{

					}
					if (getSockfd() < 0)
					{
						break;
					}
					sleep(1);
				}
			}
		}

	}

}


UdpCore::UdpCore()
{
}

UdpCore::~UdpCore()
{
	Logce("call ~UdpCore()");
	this->release();
}

/************************************************************************/
int UdpCore::createSocket()
{
	this->m_sockfd = socket(AF_INET, SOCK_DGRAM, 0);
	if (this->m_sockfd < 0) {
		Logc("socket create error,sockfd:%d err:%s %d\n",this->m_sockfd,strerror(errno),errno);
		return -1;
	}
	Logc("socket create sucess,sockfd:%d status:%s %d\n",this->m_sockfd,strerror(errno),errno);

	return this->m_sockfd;
}

int UdpCore::getSockfd()
{
	return this->m_sockfd;
}

void UdpCore::initSockAddrIn()
{
	//设置本地地址族
	memset(&local_addr, 0,sizeof(struct sockaddr_in));
	//设置协议族  Address family一般来说AF_INET（地址族）PF_INET（协议族）
	local_addr.sin_family = AF_INET;
	//IP address in network byte order（Internet address）
	local_addr.sin_addr.s_addr = htonl(INADDR_ANY);//接收任意IP发来的数据
	//Port number(必须要采用网络数据格式,普通数字可以用htons()函数转换成网络数据格式的数字)
	local_addr.sin_port = htons(this->m_port);


	//设置远程地址族
	memset(&remote_addr, 0,sizeof(struct sockaddr_in));
	remote_addr.sin_family = AF_INET;
	remote_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	remote_addr.sin_port = htons(this->m_port);
}

int UdpCore::setSockOpt(SOCKETOPT socketopt)
{
	int nb = -1;
	if (this->m_sockfd > 0) {
		//初始化Socket地址
		initSockAddrIn();
		const int opt_addr_resume = 1;
		nb = setsockopt(this->m_sockfd,SOL_SOCKET,SO_REUSEADDR,&opt_addr_resume,sizeof(opt_addr_resume));
		Logc("setsockopt SO_REUSEADDR:%d status:%s %d",nb,strerror(errno),errno);
		//设置该套接字为广播类型
		const int opt = 1;

		if (socketopt == BROADCAST) {
			nb = setsockopt(this->m_sockfd, SOL_SOCKET, SO_BROADCAST, (char*)&opt, sizeof(opt));
			Logc("setsockopt SO_BROADCAST:%d status:%s %d",nb,strerror(errno),errno);
		}
		else
		{
			memset(&m_mreq, 0, sizeof(struct ip_mreq));
			if (m_group_addr == NULL) {
				int len = strlen(GROUP_ADDR) + 1;
				m_group_addr = new char[len];
				memset(m_group_addr,0,len);
				strcpy(m_group_addr,GROUP_ADDR);
			}
			//加入该组播组
			m_mreq.imr_multiaddr.s_addr = inet_addr(m_group_addr);
			if (m_bindIp == NULL)
			{
				m_mreq.imr_interface.s_addr = htonl(INADDR_ANY);
			}
			else
			{
				//设置本地地址
				m_mreq.imr_interface.s_addr = inet_addr(m_bindIp);
			}

			nb = setsockopt(this->m_sockfd,IPPROTO_IP,IP_ADD_MEMBERSHIP,&m_mreq,sizeof(m_mreq));
			Logc("setsockopt IP_ADD_MEMBERSHIP:%d status:%s %d",nb,strerror(errno),errno);
			int loop = 0;/*设置不回环 参数loop设置为0禁止回送，设置为1允许回送*/
			nb = setsockopt(this->m_sockfd,IPPROTO_IP, IP_MULTICAST_LOOP,&loop, sizeof(loop));
			Logc("setsockopt IP_MULTICAST_LOOP:%d status:%s %d",nb,strerror(errno),errno);
		}

	}
	return nb;
}

int UdpCore::getSockOpt()
{
	int opt = -1;
	socklen_t len = sizeof(opt);
	int ret = getsockopt(this->m_sockfd,IPPROTO_IP,IP_ADD_MEMBERSHIP,&opt,&len);
	Logc("getsockopt ret:%d, opt:%d status:%s %d %d",ret, opt,strerror(errno),errno,len);
}


void UdpCore::setPort(unsigned short port)
{
	this->m_port = port;
}

//设置本地IP地址
void UdpCore::setBindIp(char *bindIp)
{
	this->m_bindIp = bindIp;
}

//设置组播组地址
void UdpCore::setGroupAddr(const char *groupAddr)
{
	int len = strlen(groupAddr) + 1;
	this->m_group_addr = new char[len];
	memset(this->m_group_addr,0,len);
	strcpy(this->m_group_addr,groupAddr);
}

char* UdpCore::getGroupAddr()
{
	return this->m_group_addr;
}

bool UdpCore::Bind()
{
	int ret = bind(m_sockfd,(struct sockaddr *)&local_addr,sizeof(struct sockaddr));
	char* ip = (char*)inet_ntoa(local_addr.sin_addr);
	int port = ntohs(local_addr.sin_port);
	Logce("bind ip:%s:%d, ret:%d status:%s %d",ip,port,ret, strerror(errno),errno);
	if(ret == -1)  {
		perror("bind err\n");
		return false;
	}
	return true;
}

/************************************************************************/
/* UDP发送数据                                                           */
/************************************************************************/
int UdpCore::sendTo(const char* ip,const short port,const char* buf,int len)
{
	struct sockaddr_in n_addr;
	//设置协议族  Address family一般来说AF_INET（地址族）PF_INET（协议族）
	n_addr.sin_family = AF_INET;
	//Port number(必须要采用网络数据格式,普通数字可以用htons()函数转换成网络数据格式的数字)
	n_addr.sin_port = htons(port);
	//IP address in network byte order（Internet address）
	n_addr.sin_addr.s_addr = inet_addr(ip);

	int ret = sendto(m_sockfd, buf, len, 0, (struct sockaddr*)&n_addr, sizeof(struct sockaddr_in));
	Logc("send data ip:%s:%d len:%d sock:%d, ret:%d, status:%s %d",ip,port,len,this->m_sockfd,ret,strerror(errno),errno);
	/*if (ret < 0) {
		perror("send error:");
		Logc("send error...ret:%d sock:%d status:%s\n",ret,this->m_sockfd,strerror(errno));
	}*/
	return ret;
}

/************************************************************************/
/* UDP接收数据  http://blog.sina.com.cn/s/blog_466f19180100004w.html     */
/************************************************************************/
int UdpCore::recvFrom(char** ip,int *port,char* buf,int len)
{
	socklen_t addr_len = sizeof(remote_addr);
	//从广播地址接受消息
	int ret = recvfrom(m_sockfd, buf, len, 0, (struct sockaddr*)&remote_addr, &addr_len);
	if (ret > 0)
	{
		*ip = (char*)inet_ntoa(remote_addr.sin_addr);
		*port = ntohs(remote_addr.sin_port);
//		Logc("Received a data from client %s, size=%d,data= %s,port=%d\n",*ip,ret,buf,*port);
		//printf("Received a string from client %s, string is: %s\n",inet_ntoa(remote_addr.sin_addr), buf);
	}
	else
	{
		Logc("recvFrom error scok:%d, ret:%d, status:%s %d",this->m_sockfd,ret,strerror(errno),errno);
	}
	return ret;
}

int UdpCore::release()
{
	int ret = m_sockfd>0?close(m_sockfd):m_sockfd;//
	//int ret = shutdown(m_sockfd,SHUT_RDWR);
	Logc("close socket ret:%d sockfd:%d and free m_group_addr:%s status:%s %d",ret,m_sockfd,m_group_addr,strerror(errno),errno);
	if (ret == 0)
	{
		this->m_sockfd = -1;
	}
	if (m_group_addr != NULL)
	{
		free(m_group_addr);
		m_group_addr = NULL;
	}
	return ret;
}
