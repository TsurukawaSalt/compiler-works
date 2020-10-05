#include <iostream>
using namespace std;

char CHAR; // 当前字符
char TOKEN[100]; // 当前字符串-->单词
char INPUT[100]; // 输入字符串-->文本
int pos; // 记录文本中字符指针的位置
int num; // 记录当前单词TOKEN中字符数

char GETCHAR(){
    return INPUT[++pos];
}

char GETNBC(){
    char c = INPUT[pos];
    while(c == ' '){
        c = INPUT[++pos];
    }
    return c;
}

void CAT(){
    // 连接单词字符串和当前读入的字符，如：TOKEN='BEGI', CHAR='N'，连接为‘BEGIN’
    TOKEN[++num] = CHAR;
}

void UNGETCH(){
    pos--;
}

bool ISLETTER(char c){
    return isalpha(c) != 0;
}

bool ISDIGIT(char c){
    return isdigit(c);
}

bool RESERVE(){
    // TOKEN保存的字符串为保留字 返回true；为标识符，返回false
    return false;
}

int ATOI(){
    // 字符串-->数字
    return stoi(TOKEN);
}

void ERROR(){
    // 出错处理
}

int main() {
    std::cout << "Please input string!" << std::endl;
    return 0;
}
