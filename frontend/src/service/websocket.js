import { Client } from "@stomp/stompjs";

const getAccessToken = () => {
    return (
        sessionStorage.getItem("prism_access_token") ||
        localStorage.getItem("prism_access_token")
    );
};

const getWebSocketUrl = () => {
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

    // Production:
    // VITE_API_BASE_URL = https://prism-ai-backend.onrender.com/api/v1
    if (apiBaseUrl && apiBaseUrl.startsWith("http")) {
        const url = new URL(apiBaseUrl);

        const protocol = url.protocol === "https:" ? "wss:" : "ws:";

        return `${protocol}//${url.host}/ws`;
    }

    // Local Vite development
    return "ws://localhost:8080/ws";
};

export function createChatClient({
                                     sessionId,
                                     onMessage,
                                     onConnect,
                                     onError,
                                 }) {
    const token = getAccessToken();

    if (!token) {
        throw new Error("Authentication token not available.");
    }

    const client = new Client({
        brokerURL: getWebSocketUrl(),

        connectHeaders: {
            Authorization: `Bearer ${token}`,
        },

        reconnectDelay: 5000,

        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,

        debug: (message) => {
            if (import.meta.env.DEV) {
                console.log("[STOMP]", message);
            }
        },

        onConnect: () => {
            console.log("[WebSocket] Connected");

            client.subscribe(
                `/topic/mentor-sessions/${sessionId}/chat`,
                (message) => {
                    try {
                        const chatMessage = JSON.parse(message.body);

                        onMessage?.(chatMessage);
                    } catch (error) {
                        console.error(
                            "[WebSocket] Failed to parse chat message:",
                            error
                        );
                    }
                }
            );

            console.log(
                `[WebSocket] Subscribed to session ${sessionId}`
            );

            onConnect?.();
        },

        onStompError: (frame) => {
            console.error(
                "[WebSocket] STOMP error:",
                frame.headers["message"]
            );

            console.error(
                "[WebSocket] STOMP details:",
                frame.body
            );

            onError?.(
                new Error(
                    frame.headers["message"] ||
                    "WebSocket connection error."
                )
            );
        },

        onWebSocketError: (error) => {
            console.error(
                "[WebSocket] Connection error:",
                error
            );

            onError?.(
                new Error(
                    "Unable to establish WebSocket connection."
                )
            );
        },

        onWebSocketClose: (event) => {
            console.log(
                "[WebSocket] Connection closed:",
                event.code,
                event.reason
            );
        },
    });

    client.activate();

    return client;
}

export function sendChatMessage(client, sessionId, content) {
    if (!client || !client.connected) {
        throw new Error("WebSocket is not connected.");
    }

    client.publish({
        destination: `/app/mentor-sessions/${sessionId}/chat`,
        body: JSON.stringify({
            content,
        }),
    });
}