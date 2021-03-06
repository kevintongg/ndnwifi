package no.bouvet.p2pcommunication.singlesocket;

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */

class GroupOwnerSocketHandler extends Thread {

  private static final String TAG = "GroupOwnerSocketHandler";
  private final int THREAD_COUNT = 10;

  /**
   * A ThreadPool for client sockets.
   */

  private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
      THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
      new LinkedBlockingQueue<>());
  private ServerSocket socket = null;
  private Handler handler;

  public GroupOwnerSocketHandler(Handler handler) throws IOException {
    try {
      socket = new ServerSocket(4545);
      this.handler = handler;
      Log.d("GroupOwnerSocketHandler", "Socket Started");
    } catch (IOException e) {
      e.printStackTrace();
      pool.shutdownNow();
      throw e;
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        // A blocking operation. Initiate a ChatManager instance when
        // there is a new connection
        pool.execute(new ChatManager(socket.accept(), handler));
        Log.d(TAG, "Launching the I/O handler");

      } catch (IOException e) {
        try {
          if (socket != null && !socket.isClosed()) {
            socket.close();
          }
        } catch (IOException ignored) {

        }
        e.printStackTrace();
        pool.shutdownNow();
        break;
      }
    }
  }
}
