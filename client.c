#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>

#define  BUF_SIZE   1024
void error_handling(char *message);

int main( int argc, char *argv[])
{
   char send_socket[BUF_SIZE]= {0,};
   int sock,j,i, k;
   char message[BUF_SIZE];
   char buf[BUF_SIZE];
   char buff[256];

   struct sockaddr_in serv_adr;
   FILE *fp;
   FILE *fp1;
   if(argc != 3) {
      printf("Usage : %s <IP> <PORT>\n", argv[0]);
      exit(1);
   }
   
   sock= socket( PF_INET, SOCK_STREAM, 0);
   if( -1 ==sock)
      error_handling("socket() error");

   memset( &serv_adr, 0, sizeof( serv_adr));
   serv_adr.sin_family     = AF_INET;
   serv_adr.sin_addr.s_addr= inet_addr(argv[1]);
   serv_adr.sin_port       = htons(atoi(argv[2]));
  

   if( -1 == connect(sock, (struct sockaddr*)&serv_adr, sizeof(serv_adr) ) )
   {
      printf( "접속 실패\n");
      exit( 1);
   }
   
   
   while(1){
      fp = fopen("send.txt","r"); 
      fscanf(fp, "%s",message); 
      
      fclose(fp);
      if(strcmp(message,send_socket)==0)
      {
      	continue;
      }
      else{         
         strcpy(send_socket,message);
         printf("SEND : %s\n", send_socket);
         write(sock,message,sizeof(message)-1);
         sleep(0.5);
      }
      for (j = 0; j < 100; j++) 
         {
         message[j] = 0; //100글자까지 초기화
	      }
   }
   close(sock);
   return 0;
}

void error_handling(char *message)
{
   fputs(message,stderr);
   fputc('\n',stderr);
   exit(1);
}
