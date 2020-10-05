FROM gcc:10
WORKDIR /SimpleLA/
COPY ./* ./
RUN gcc main.c -o main
RUN chmod +x main