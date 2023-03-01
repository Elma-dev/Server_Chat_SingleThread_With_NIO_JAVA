import select,socket,sys,queue

server=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
server.setblocking(0)
server.bind(('localhost',1234))
server.listen()
inputs=[server]
outputs=[]
msg_queues={}

def startServer():
    while inputs :
        redable,writble,exceptional=select.select(inputs,outputs,inputs)
        for s in redable:
            if s is server:
                connection,client_address=s.accept()
                connection.setblocking(0)
                inputs.append(connection)
                msg_queues[connection]=queue.Queue()
            else:
                data=s.recv(1024)
                if data:
                    msg_queues[s].put[data]
                    if s not in outputs:
                        outputs.append(s)
                    else:
                        if s in outputs:
                            outputs.remove(s)
                        inputs.remove(s)
                        s.close()
                        del msg_queues[s]
        for s in writble:
            try:
                next_msg=msg_queues[s].get_nowait()
            except queue.Empty:
                outputs.remove(s)
            else:
                message=next_msg.decode().replace("\n","")
                message=message.replace("\r","")
                response="Size="+str(len(message))+"\n"
                s.send(response.encode())
        for s in exceptional:
            inputs.remove(s)
            if s in outputs:
                outputs.remove(s)
            s.close()
            del msg_queues[s]

if __name__ == '__main__':
    startServer()