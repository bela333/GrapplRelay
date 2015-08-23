package io.grappl.server.host.exclient;

import io.grappl.server.host.Host;
import io.grappl.server.logging.Log;

import java.io.*;
import java.net.Socket;

public class ExClient {

    private Host host;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private PrintStream printStream;
    private boolean isClosed;
    private ExClientData exClientData;

    public ExClient(Host host, Socket socket) {
        this.host = host;
        this.socket = socket;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            printStream = new PrintStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Host getHost() {
        return host;
    }

    public void start() {
        Log.debug("ExClient connected from " + socket.getInetAddress().toString() + ":" + socket.getPort());
        Log.debug(getHost().getApplicationSocket().getLocalPort() + ":" + getHost().getExClientList().size() + " clients connected");
        final Socket local = socket;

        /* Start imported old code */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get traffic socket.
                    final Socket remote = host.getMessageSocket().accept();

                    Thread localToRemote = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] buffer = new byte[4096];
                            int size;

                            try {
                                while ((size = local.getInputStream().read(buffer)) != -1) {
                                    remote.getOutputStream().write(buffer, 0, size);

                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                try {
                                    local.close();
                                    remote.close();
                                    close();
                                } catch (IOException e1) {
//                                    e1.printStackTrace();
                                }
                            }

                            try {
                                local.close();
                                remote.close();
                                close();
                            } catch (IOException e) {
//                                e.printStackTrace();
                            }
                        }
                    });
                    localToRemote.start();

                    final Thread remoteToLocal = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] buffer = new byte[4096];
                            int size;

                            try {
                                while ((size = remote.getInputStream().read(buffer)) != -1) {
                                    local.getOutputStream().write(buffer, 0, size);

                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                try {
                                    local.close();
                                    remote.close();
                                    close();
                                } catch (IOException e1) {
//                                    e1.printStackTrace();
                                }
                            }

                            try {
                                local.close();
                                remote.close();
                                close();
                            } catch (IOException e) {
//                                e.printStackTrace();
                            }
                        }
                    });
                    remoteToLocal.start();

                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }).start();
        /* End imported old code */
    }

    public void close() {
        if(!isClosed) {
            isClosed = true;
            Log.debug("ExClient disconnected from " + socket.getInetAddress().toString() + ":" + socket.getPort());
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            host.disassociate(this);
        }
    }

    /**
     * Determines if the connection is still alive or not
     */
    public boolean ping() {
        try {
            try {
                dataOutputStream.writeByte(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public Socket getSocket() {
        return socket;
    }
}
