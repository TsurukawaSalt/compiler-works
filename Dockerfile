FROM gcc:10
WORKDIR /SimpleLA/
COPY ./* ./
RUN gcc main.cpp -o main
RUN chmod +x main