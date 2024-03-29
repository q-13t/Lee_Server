
/**imports */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Listens for connection for Application {@code Lee} on android.
 * </p>
 * Expects to receive {@linkplain Server#path_to_maps path} to map/folder with
 * maps
 * 
 * @author Volodymyr Davybida
 * 
 */
public class Server {
    private static ServerSocket server = null;
    private static final int PORT = 4000;
    public static int clients_connected = 0;
    public static String path_to_maps = "";
    public static ExecutorService executor_service = Executors.newCachedThreadPool();
    public static ArrayList<String> maps = new ArrayList<>();
    public static String maps_str = "";
    private static boolean found = false;

    /**
     * Main function that creates and listens on {@linkplain Server#server
     * ServerSocket}, awaiting for
     * clients to connect
     * 
     * @param args path to directory with maps eg. E://maps or E:/maps
     * @throws Exception god i hope nothing
     */
    public static void main(String[] args) {
        try {

            if (args.length != 1) {
                System.out.println("YOU NEED TO SPECIFY PATH TO MAPS!");
                Thread.sleep(3000);
                System.exit(1);
            } else {
                path_to_maps = args[0];
                check_dir();
            }

            log("\nPath provided -> " + path_to_maps + "\n");

            log("Found maps: \n");

            for (String string : maps) {
                log(string);
            }

            server = new ServerSocket(PORT, 100, InetAddress.getLocalHost());

            Socket client = null;
            log("\nServer started and listens on IP -> "
                    + server.getInetAddress().toString()
                    + " and PORT -> "
                    + server.getLocalPort() + "\n");
            while (true) {
                if ((client = server.accept()) != null) {
                    clients_connected++;
                    String client_name = "Client: " + clients_connected;
                    log(client_name + " connected!");
                    new ClientConnectionThread(client, client_name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks wether provided {@linkplain Server#path_to_maps path} is directory or
     * map itself.
     */
    private static void check_dir() {
        try {
            File file = new File(path_to_maps);
            if (file.isDirectory()) {
                check_files(false);
            } else {
                if (!file.getName().matches("s_map_.+.txt")) {
                    System.out.println("Path specified does not lead to map!");
                    Thread.sleep(3000);
                    System.exit(1);
                } else {
                    maps.add(Paths.get(path_to_maps).toFile().getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Using provided {@linkplain Server#path_to_maps path} checks wether specified
     * directory has maps in it.
     * If so memorizes them, else ends program notifying user.
     */
    private static void check_files(boolean contains) {
        if (contains) {
            try {
                Path path = Paths.get(path_to_maps);
                Files.walk(path).forEach(x -> {
                    if (x.toFile().getName().matches("s_map_.+.txt")) {
                        maps.add(x.toFile().getName());
                        maps_str += x.toFile().getName() + "|";
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Path path = Paths.get(path_to_maps);

                Files.walk(path).forEach(x -> {
                    if (x.toFile().getName().matches("s_map_..txt")) {
                        found = true;
                    }
                });
                if (found) {
                    check_files(found);
                } else {
                    System.out.println("No maps were found!");
                    Thread.sleep(3000);
                    System.exit(1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Prints log for server
     * 
     * @param msg to be displayed
     */
    public static void log(String msg) {
        System.out.println();
        System.out.println(msg);
    }

    /**
     * Returns requested by {@linkplain ClientConnectionThread#socket client} map.
     * 
     * @param map_name
     * @return map in string line
     */
    public static String get_map(String map_name) {
        File map = new File(path_to_maps, map_name);
        String line = "";
        String map_str_2 = "";
        int X = 0;
        int Y = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(map))) {
            while ((line = br.readLine()) != null) {
                Y = line.length();
                X++;
                map_str_2 += line + "|";

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return place_points(map_str_2, X, Y);
    }

    /**
     * Places randomly points {@code @ (player)} and {@code $ (goal)} at provided
     * map
     * 
     * @param map points to be placed in
     * @param x   dimensions
     * @param y   dimensions
     * @return modified map with points
     */
    private static String place_points(String map, int x, int y) {

        String[] maps_splitted = map.split("\\|");

        char[][] map_in_chars = new char[x][y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                map_in_chars[i][j] = maps_splitted[i].charAt(j);
            }
        }
        boolean contains_goal;
        boolean contains_player;

        do {

            contains_goal = false;
            contains_player = false;

            int x_1 = (int) (Math.random() * x);
            int y_1 = (int) (Math.random() * y);
            int x_2 = (int) (Math.random() * x);
            int y_2 = (int) (Math.random() * y);
            if (x_1 != x_2 && y_1 != y_2) {
                if (map_in_chars[x_1][y_1] != ' ') {
                    contains_player = true;
                }

                if (map_in_chars[x_2][y_2] != ' ') {
                    contains_goal = true;
                }

                if (contains_player && contains_goal) {
                    map_in_chars[x_1][y_1] = '@';
                    map_in_chars[x_2][y_2] = '$';
                }

            }

        } while (!contains_player || !contains_goal);

        String out_str = "";
        for (int i = 0; i < map_in_chars.length; i++) {
            for (int j = 0; j < map_in_chars[i].length; j++) {
                out_str += map_in_chars[i][j];
            }
            out_str += "|";
        }
        return out_str;

    }
}

/**
 * Handles User requests until they disconnect
 * 
 * @author Volodymyr Davybida
 * @see java.lang.Runnable
 */
class ClientConnectionThread implements Runnable {
    private Socket socket = null;
    private String client_name = "";

    public ClientConnectionThread(Socket socket, String client_name) {
        this.client_name = client_name;
        this.socket = socket;
        Server.executor_service.execute(this);
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Server.log("Sending list of maps to " + client_name);
            out.println(Server.maps_str);
            out.flush();
            String line = "";
            while (socket.isConnected()) {
                line = in.readLine();

                Server.log(client_name + " request -> " + line);
                if (!line.equals("DISCONNECT") && line.contains("s_map")) {

                    String map = Server.get_map(line);

                    Server.log("Sending map -> \n" + map.replaceAll("\\|", "\n") + "To -> " + client_name);
                    out.println(map);
                    out.flush();

                    line = in.readLine();
                    Server.log(client_name + " response -> " + line);

                } else if (line.equals("DISCONNECT")) {
                    break;
                }
            }

        } catch (NullPointerException n) {
            Server.log(client_name + " did a BRUH moment!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Server.clients_connected--;
                socket.close();
                Server.log(client_name + " disconnected!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}