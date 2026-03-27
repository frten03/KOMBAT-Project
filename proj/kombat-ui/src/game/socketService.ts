import { Client, IMessage } from '@stomp/stompjs'
import type { GameStateDTO } from '../types/gameTypes'

const WS_URL = 'http://localhost:8080/ws'

type GameEventCallback = (dto: GameStateDTO) => void

class WebSocketService {
    private client: Client | null = null
    private gameEventListeners: Map<string, GameEventCallback[]> = new Map()
    private currentGameId: string | null = null

    connect(): void {
        if (this.client?.connected) return

        this.client = new Client({
            brokerURL: WS_URL,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        })

        this.client.onConnect = () => {
            console.log('[WebSocket] Connected')
            if (this.currentGameId) {
                this.subscribeToGame(this.currentGameId)
            }
        }

        this.client.onDisconnect = () => {
            console.log('[WebSocket] Disconnected')
        }

        this.client.onStompError = (frame) => {
            console.error('[WebSocket] STOMP Error:', frame.body)
        }

        this.client.activate()
    }

    disconnect(): void {
        if (this.currentGameId) {
            this.unsubscribeFromGame(this.currentGameId)
        }
        if (this.client) {
            this.client.deactivate()
            this.client = null
        }
        this.gameEventListeners.clear()
    }

    subscribeToGame(gameId: string): void {
        if (!this.client?.connected) {
            this.currentGameId = gameId
            this.connect()
            return
        }

        this.currentGameId = gameId

        // subscribe to game state updates
        this.client.subscribe(`/topic/game/${gameId}/state`, (message: IMessage) => {
            try {
                const dto = JSON.parse(message.body) as GameStateDTO
                this.notifyListeners('gameStateUpdated', dto)
            } catch (e) {
                console.error('[WebSocket] Failed to parse game state:', e)
            }
        })

        // subscribe to game over
        this.client.subscribe(`/topic/game/${gameId}/gameOver`, (message: IMessage) => {
            try {
                const data = JSON.parse(message.body)
                this.notifyListeners('gameOver', data as { winner: string; reason: string })
            } catch (e) {
                console.error('[WebSocket] Failed to parse game over:', e)
            }
        })

        // subscribe to turn changes
        this.client.subscribe(`/topic/game/${gameId}/turn`, (message: IMessage) => {
            try {
                const data = JSON.parse(message.body)
                this.notifyListeners('turnChanged', data as { currentPlayer: number; turn: number })
            } catch (e) {
                console.error('[WebSocket] Failed to parse turn change:', e)
            }
        })

        console.log(`[WebSocket] Subscribed to game ${gameId}`)
    }

    unsubscribeFromGame(gameId: string): void {
        if (!this.client?.connected) return

        this.client.unsubscribe(`/topic/game/${gameId}/state`)
        this.client.unsubscribe(`/topic/game/${gameId}/gameOver`)
        this.client.unsubscribe(`/topic/game/${gameId}/turn`)

        console.log(`[WebSocket] Unsubscribed from game ${gameId}`)
    }

    on(event: string, callback: GameEventCallback): void {
        if (!this.gameEventListeners.has(event)) {
            this.gameEventListeners.set(event, [])
        }
        this.gameEventListeners.get(event)?.push(callback)
    }

    off(event: string, callback: GameEventCallback): void {
        const listeners = this.gameEventListeners.get(event)
        if (listeners) {
            const index = listeners.indexOf(callback)
            if (index > -1) {
                listeners.splice(index, 1)
            }
        }
    }

    private notifyListeners(event: string, data: unknown): void {
        const listeners = this.gameEventListeners.get(event)
        if (listeners) {
            listeners.forEach(callback => callback(data as GameStateDTO))
        }
    }

    isConnected(): boolean {
        return this.client?.connected ?? false
    }
}

// singleton instance
export const webSocketService = new WebSocketService()

// hook for using websocket in components
import { useEffect, useCallback, useRef } from 'react'
import { useGameStore, dtoToFrontend } from '../store/gameStore'

export function useWebSocket(gameId: string | null) {
    const store = useGameStore()
    const isSubscribedRef = useRef(false)

    const handleGameStateUpdate = useCallback((dto: GameStateDTO) => {
        console.log('[WebSocket] Game state updated')
        if (store.gameState) {
            store.setGameState(dtoToFrontend(dto, store.gameState.mode))
        }
    }, [store])

    const handleGameOver = useCallback((data: { winner: string; reason: string }) => {
        console.log('[WebSocket] Game over:', data)
        store.setGameOver(data.reason, data.winner)
    }, [store])

    useEffect(() => {
        if (!gameId) return

        // connect and subscribe
        webSocketService.connect()
        
        // small delay to ensure connection is established
        const timeout = setTimeout(() => {
            webSocketService.subscribeToGame(gameId)
            isSubscribedRef.current = true
        }, 100)

        webSocketService.on('gameStateUpdated', handleGameStateUpdate)
        webSocketService.on('gameOver', handleGameOver)

        return () => {
            clearTimeout(timeout)
            if (isSubscribedRef.current) {
                webSocketService.unsubscribeFromGame(gameId)
                isSubscribedRef.current = false
            }
            webSocketService.off('gameStateUpdated', handleGameStateUpdate)
            webSocketService.off('gameOver', handleGameOver)
        }
    }, [gameId, handleGameStateUpdate, handleGameOver])
}

// Keep old export name for backwards compatibility
export const useSocket = useWebSocket
