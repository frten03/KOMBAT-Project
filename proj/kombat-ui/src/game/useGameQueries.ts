import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import type { GameStateDTO, GameMode } from '../types/gameTypes'
import {
    getGameState,
    nextTurn,
    purchaseHex,
    spawnMinion,
    surrender,
    initialSpawn,
    startAutoPlay,
    stopAutoPlay,
} from './gameApi'
import { dtoToFrontend } from '../store/gameStore'

// Query Keys
export const gameKeys = {
    all: ['game'] as const,
    state: (gameId: string) => ['game', 'state', gameId] as const,
    config: ['game', 'config'] as const,
}

// Fetch game state with React Query
export function useGameStateQuery(gameId: string, mode: GameMode, enabled = true) {
    return useQuery({
        queryKey: gameKeys.state(gameId),
        queryFn: async (): Promise<{ dto: GameStateDTO; frontend: ReturnType<typeof dtoToFrontend> }> => {
            const dto = await getGameState(gameId)
            return {
                dto,
                frontend: dtoToFrontend(dto, mode),
            }
        },
        enabled: enabled && !!gameId,
        refetchInterval: (query) => {
            // Stop polling if game is finished
            const state = query.state.data?.dto
            if (state?.phase === 'FINISHED') return false
            return 2000 // Poll every 2 seconds
        },
    })
}

// Mutations
export function useNextTurnMutation(gameId: string, mode: GameMode) {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: () => nextTurn(gameId),
        onSuccess: (dto) => {
            queryClient.setQueryData(gameKeys.state(gameId), {
                dto,
                frontend: dtoToFrontend(dto, mode),
            })
        },
    })
}

export function usePurchaseHexMutation(gameId: string, mode: GameMode) {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: ({ row, col }: { row: number; col: number }) =>
            purchaseHex(gameId, row, col),
        onSuccess: (dto) => {
            queryClient.setQueryData(gameKeys.state(gameId), {
                dto,
                frontend: dtoToFrontend(dto, mode),
            })
        },
    })
}

export function useSpawnMinionMutation(gameId: string, mode: GameMode) {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: ({ typeName, row, col }: { typeName: string; row: number; col: number }) =>
            spawnMinion(gameId, typeName, row, col),
        onSuccess: (dto) => {
            queryClient.setQueryData(gameKeys.state(gameId), {
                dto,
                frontend: dtoToFrontend(dto, mode),
            })
        },
    })
}

export function useInitialSpawnMutation(gameId: string, mode: GameMode) {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: ({ typeName, row, col }: { typeName: string; row: number; col: number }) =>
            initialSpawn(gameId, typeName, row, col),
        onSuccess: (dto) => {
            queryClient.setQueryData(gameKeys.state(gameId), {
                dto,
                frontend: dtoToFrontend(dto, mode),
            })
        },
    })
}

export function useSurrenderMutation(gameId: string) {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: () => surrender(gameId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: gameKeys.all })
        },
    })
}

export function useAutoPlayMutation(gameId: string) {
    return useMutation({
        mutationFn: ({ action }: { action: 'start' | 'stop' }) =>
            action === 'start' ? startAutoPlay(gameId) : stopAutoPlay(gameId),
    })
}

// Prefetch helpers
export function usePrefetchGameState() {
    const queryClient = useQueryClient()

    return (gameId: string, mode: GameMode) => {
        queryClient.prefetchQuery({
            queryKey: gameKeys.state(gameId),
            queryFn: async () => {
                const dto = await getGameState(gameId)
                return {
                    dto,
                    frontend: dtoToFrontend(dto, mode),
                }
            },
            staleTime: 1000,
        })
    }
}
