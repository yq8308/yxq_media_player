// AndroidTCPServer.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <winsock2.h>
#pragma comment(lib,"ws2_32.lib")
#include <vector>

std::vector<SOCKET> vector;

int work(void* p){
	SOCKET sock = (SOCKET)p;
	while (sock)
	{
		char recvBuf[1024];
		memset(recvBuf, 0, 1024);
		int ret = recv(sock, recvBuf, 1024, 0);

		if (ret == 0)
		{
			printf("c--> socket : %d 断开连接! \n",sock);
			std::vector<SOCKET>::iterator itr = vector.begin();
			while (itr != vector.end())
			{
				if (*itr == sock)
				{
					vector.erase(itr);
					closesocket(sock);
					return 0;
				}
				else
					itr++;
			}
		}

		if (strcmp(recvBuf,"")==0)
		{
			continue;
		}

		printf("c--> 接收消息 from=[%d]：%s \n",sock, recvBuf);
			
		for (std::vector<SOCKET>::iterator it = vector.begin(); it != vector.end(); it++)
		{
			SOCKET s = (SOCKET)*it;
			int len = send(s, recvBuf, strlen(recvBuf) + 1, 0);
			if (len == SOCKET_ERROR)
			{
				vector.erase(it);
				closesocket(sock);
				printf("s--> ( ERROR ) 发送消息失败，连接已断开！\n");
				return 0;
			}
			printf("s--> 发送消息 to=[%d]：%s  长度=[%d] \n", s, recvBuf, len);
			Sleep(10);
		}
	}
	
	return 0;
}

int _tmain(int argc, _TCHAR* argv[])
{
	WSADATA wsa;
	if (WSAStartup(MAKEWORD(1, 1), &wsa) != 0)
	{
		return -1;
	}


	unsigned long ul = 1;

	SOCKET sockSrv = socket(AF_INET, SOCK_STREAM, 0);

	int ret = ioctlsocket(sockSrv, FIONBIO, (unsigned long *)&ul);

	SOCKADDR_IN addrSrv;
	addrSrv.sin_addr.S_un.S_addr = htonl(INADDR_ANY);
	addrSrv.sin_family = AF_INET;
	addrSrv.sin_port = htons(6000);

	bind(sockSrv, (SOCKADDR*)&addrSrv, sizeof(SOCKADDR));

	listen(sockSrv, 20);

	SOCKADDR_IN addrClient;
	int len = sizeof(SOCKADDR);
	
	while (1)
	{
		if (vector.size()>20)
		{
			Sleep(100);
		}
		SOCKET sockConn = accept(sockSrv, (SOCKADDR*)&addrClient, &len);
		if (sockConn != SOCKET_ERROR)
		{
			printf("s-->  1个新连接: [socket] = [%d] \n",sockConn);
			vector.push_back(sockConn);
			HANDLE h = CreateThread(NULL, NULL, (LPTHREAD_START_ROUTINE)work, (LPVOID)sockConn, NULL, NULL);
			if (h==NULL)
			{
				printf("创建线程失败\n");
			}
		}
	}
	
	WSACleanup();


	return 0;
}

