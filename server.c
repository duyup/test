#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>

#define  BUF_SIZE   1024
void ErrorHandling(char *message);

int main(int argc, char *argv[])

{
   FILE *fp;
   int hserv_sock, hclnt_sock;
   char buf[BUF_SIZE];
   char write_memo[BUF_SIZE] = { 0, };
   int str_len, j, i;

   struct sockaddr_in serv_adr, clnt_adr;
   socklen_t clnt_adr_sz;

   if (argc != 2)
   {
      printf("Usage:%s <port>\n", argv[0]);
      exit(1);
   }

   hserv_sock = socket(PF_INET, SOCK_STREAM, 0); //소켓생성

   if (hserv_sock == -1)
      ErrorHandling("socket() error");

   memset(&serv_adr, 0, sizeof(serv_adr));
   serv_adr.sin_family = AF_INET;
   serv_adr.sin_addr.s_addr = htonl(INADDR_ANY);
   serv_adr.sin_port = htons(atoi(argv[1]));

   if (bind(hserv_sock, (struct sockaddr*)&serv_adr, sizeof(serv_adr)) == -1) //IP주소와 PORT 번호 할당
      ErrorHandling("bind() error");

   if (listen(hserv_sock, 5) == -1) //listen 소켓을 서버 소켓으로 완성
      ErrorHandling("listen() error");


   clnt_adr_sz = sizeof(clnt_adr);

   hclnt_sock = accept(hserv_sock, (struct sockaddr*)&clnt_adr, &clnt_adr_sz); // accept함수 호출
   if (hclnt_sock == -1)
      ErrorHandling("accept() error");

   while (1)
   {
      str_len=read(hclnt_sock, buf, BUF_SIZE); //클라이언트 메세지를 받음
      if (strcmp(write_memo, buf) == 0) // *참고
         continue;
      else {
         fp = fopen("do.txt", "w"); //fopen함수 이용하여 메모장 쓰기모드로 열기
         fwrite(buf, strlen(buf), 1, fp); // 받은 메세지를 메모장에 쓰기
         fclose(fp); // 메모장을 닫는다.
         strcpy(write_memo, buf); // *받은 메세지를 write_memo 변수에 복사해놓음(계속 쓰는것을 방지)
         sleep(0.5);
         printf("Message from Client : %s\n", buf); //받은 메세지를 표시
      }
      for (j = 0; j < 100; j++) {
         buf[j] = 0; //100글자까지 초기화
      }
	
         
   }
   close(hclnt_sock);
   close(hserv_sock);
   return 0;
}

void ErrorHandling(char *buf)
{
   fputs(buf, stderr);
   fputc('\n', stderr);
   exit(1);
}
