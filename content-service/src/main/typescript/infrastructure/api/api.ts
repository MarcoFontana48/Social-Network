import express from "express";
import http from "http";
import cors from "cors";
import { Server as SocketIOServer } from 'socket.io';

export class Server {
    private readonly port: number;
    private readonly middlewares: express.RequestHandler[];
    private readonly router: express.Router;
    public server?: http.Server;
    private io?: SocketIOServer;

    constructor(port: number, middlewares: express.RequestHandler[], router: express.Router) {
        this.port = port;
        this.middlewares = middlewares;
        this.router = router;
    }

    async start(onStarted: () => void = () => {}) {
        return new Promise<void>((resolve) => {
            const app = express();

            app.use(cors({
                origin: 'http://localhost:5173',
                methods: ['GET', 'POST', 'PUT', 'OPTIONS'],
                allowedHeaders: ['Content-Type', 'Authorization']
            }));

            app.use(...this.middlewares);
            app.use("/", this.router);

            this.server = http.createServer(app);

            this.io = new SocketIOServer(this.server, {
                cors: {
                    origin: 'http://localhost:5173',
                    methods: ['GET', 'POST', 'PUT', 'OPTIONS'],
                }
            });

            // Setup Socket.io connection logic
            this.io.on('connection', (socket) => {
                console.log('A user connected');

                // Listen for the joinRoom event to bind a user to a post
                socket.on('joinRoom', (postId) => {
                    socket.join(postId); // Join a room based on the post ID
                    console.log(`User joined room for post: ${postId}`);
                });

                // Listen for messages related to a specific post
                socket.on('sendMessage', (postId, messageData) => {
                    console.log(`Received message for post ${postId}:`, messageData);
                    // Broadcast the message only to clients in the room for the specific post
                    this.io?.to(postId).emit('newMessage', messageData);
                });

                // Handle disconnections
                socket.on('disconnect', () => {
                    console.log('A user disconnected');
                });
            });

            this.server.listen(this.port, "0.0.0.0", () => {
                console.log(`Server is running on port ${this.port}`);
                onStarted();
                resolve();
            });
        });
    }

    async stop() {
        return new Promise<void>((resolve) => {
            if (this.server) {
                this.server.close(() => {
                    resolve();
                });
            } else {
                resolve();
            }
        });
    }
}

export const EmptyRouter = express.Router();
export const DefaultMiddlewares: express.RequestHandler[] = [
    express.json(),
];
