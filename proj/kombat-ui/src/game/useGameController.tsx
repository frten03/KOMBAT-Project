import { useCallback, useEffect } from 'react'
import type { GameMode, HexCoord, GameStateDTO } from '../types/gameTypes'
import { useNavigate } from 'react-router-dom'
import { useGameStore, dtoToFrontend, getAdjacentHexes } from '../store/gameStore'
import {
    useGameStateQuery,
    useNextTurnMutation,
    usePurchaseHexMutation,
    useSpawnMinionMutation,
    useInitialSpawnMutation,
    useSurrenderMutation,
    useAutoPlayMutation,
} from './useGameQueries'
import { useWebSocket } from './socketService'

function shortenError(err: unknown): string {
    const msg = String(err)
    if (msg.includes('Insufficient')) return 'Not enough gold'
    if (msg.includes('Invalid') || msg.includes('spawnable')) return 'Invalid location'
    if (msg.includes('already') || msg.includes('occupied')) return 'Hex occupied'
    if (msg.includes('limit')) return 'Spawn limit reached'
    if (msg.includes('not your turn')) return 'Not your turn'
    if (msg.includes('403') || msg.includes('Forbidden')) return 'Action not allowed'
    return msg.length > 50 ? msg.substring(0, 50) + '...' : msg
}

export function useGameController(mode: GameMode, gameId: string) {
    const navigate = useNavigate()
    
    const setGameState = useGameStore(s => s.setGameState)
    const setAvailableMinionTypes = useGameStore(s => s.setAvailableMinionTypes)
    const setCurrentPlayerBudget = useGameStore(s => s.setCurrentPlayerBudget)
    const setAdjacentHexes = useGameStore(s => s.setAdjacentHexes)
    const setGameId = useGameStore(s => s.setGameId)
    const setMode = useGameStore(s => s.setMode)
    const setLoading = useGameStore(s => s.setLoading)
    const setError = useGameStore(s => s.setError)
    const setGameOver = useGameStore(s => s.setGameOver)
    const showErrorFn = useGameStore(s => s.showError)
    const closeErrorModal = useGameStore(s => s.closeErrorModal)
    const selectedHex = useGameStore(s => s.selectedHex)
    const setSelectedHex = useGameStore(s => s.setSelectedHex)
    const selectedMinionType = useGameStore(s => s.selectedMinionType)
    const setSelectedMinionType = useGameStore(s => s.setSelectedMinionType)
    const isAutoPlaying = useGameStore(s => s.isAutoPlaying)
    const setIsAutoPlaying = useGameStore(s => s.setIsAutoPlaying)
    const hasBoughtHex = useGameStore(s => s.hasBoughtHex)
    const setHasBoughtHex = useGameStore(s => s.setHasBoughtHex)
    const hasSpawnedMinion = useGameStore(s => s.hasSpawnedMinion)
    const setHasSpawnedMinion = useGameStore(s => s.setHasSpawnedMinion)
    const gameState = useGameStore(s => s.gameState)
    const loading = useGameStore(s => s.loading)
    const error = useGameStore(s => s.error)
    const availableMinionTypes = useGameStore(s => s.availableMinionTypes)
    const currentPlayerBudget = useGameStore(s => s.currentPlayerBudget)
    const adjacentHexes = useGameStore(s => s.adjacentHexes)
    const showErrorModal = useGameStore(s => s.showErrorModal)
    const errorMessage = useGameStore(s => s.errorMessage)
    const showBuyConfirm = useGameStore(s => s.showBuyConfirm)
    const showSpawnConfirm = useGameStore(s => s.showSpawnConfirm)
    const setShowBuyConfirm = useGameStore(s => s.setShowBuyConfirm)
    const setShowSpawnConfirm = useGameStore(s => s.setShowSpawnConfirm)

    // Connect to websocket for real-time updates
    useWebSocket(gameId)

    // React Query for game state with smart polling
    const {
        data: queryData,
        isLoading,
        error: queryError,
        refetch,
    } = useGameStateQuery(gameId, mode, !!gameId)

    // Mutations with automatic cache updates
    const nextTurnMutation = useNextTurnMutation(gameId, mode)
    const purchaseMutation = usePurchaseHexMutation(gameId, mode)
    const spawnMutation = useSpawnMinionMutation(gameId, mode)
    const initialSpawnMutation = useInitialSpawnMutation(gameId, mode)
    const surrenderMutation = useSurrenderMutation(gameId)
    const autoPlayMutation = useAutoPlayMutation(gameId)

    // Sync query data to Zustand store
    useEffect(() => {
        if (queryData) {
            const prevPlayer = gameState?.currentPlayer
            const prevPhase = gameState?.phase
            const newPlayer = queryData.dto.currentPlayer
            const newPhase = queryData.dto.phase
            
            setGameState(queryData.frontend)
            setAvailableMinionTypes(queryData.dto.availableMinionTypes)
            setCurrentPlayerBudget(queryData.dto.budget)

            // Reset action flags when:
            // 1. Phase changes from SETUP to RUNNING
            // 2. Turn changes between players
            // 3. SOLITAIRE mode: always reset when it's P1's turn
            const isSolitaire = mode === 'SOLITAIRE'
            const phaseChanged = prevPhase === 'SETUP' && newPhase === 'RUNNING'
            const turnChanged = prevPlayer !== undefined && prevPlayer !== newPlayer
            const p1TurnStart = isSolitaire && newPlayer === 1
            
            if (phaseChanged || turnChanged || p1TurnStart) {
                setHasBoughtHex(false)
                setHasSpawnedMinion(false)
            }

            const playerSpawnable =
                queryData.dto.currentPlayer === 1
                    ? queryData.dto.player1SpawnableHexes
                    : queryData.dto.player2SpawnableHexes
            setAdjacentHexes(getAdjacentHexes(playerSpawnable))

            // Check for game over
            if (queryData.dto.phase === 'FINISHED') {
                const winner = determineWinner(queryData.dto)
                setGameOver(queryData.dto.gameOverReason || 'Game Over', winner)
                navigate('/end')
            }
        }
    }, [queryData, navigate, setGameState, setAvailableMinionTypes, setCurrentPlayerBudget, 
        setAdjacentHexes, setGameOver, setHasBoughtHex, setHasSpawnedMinion, gameState?.currentPlayer, mode])

    // Initialize store on mount
    useEffect(() => {
        setGameId(gameId)
        setMode(mode)
        setLoading(true)
    }, [gameId, mode, setGameId, setMode, setLoading])

    // Update loading and error state
    useEffect(() => {
        setLoading(isLoading)
        if (queryError) {
            setError(queryError.message)
        }
    }, [isLoading, queryError, setLoading, setError])

    // Auto-start auto-play for AUTO mode (bot vs bot)
    useEffect(() => {
        if (mode === 'AUTO' && gameId && !isLoading && queryData) {
            if (!isAutoPlaying && queryData.dto.phase !== 'FINISHED') {
                startAutoPlay()
            }
        }
    }, [mode, gameId, isLoading, isAutoPlaying, queryData])

    const handleSelectHex = useCallback(
        (hex: HexCoord) => {
            if (hex === selectedHex) {
                setSelectedHex(null)
                setShowBuyConfirm(false)
                setShowSpawnConfirm(false)
            } else {
                setSelectedHex(hex)
                // Check if can spawn (owned by player, no minion, has minion type selected)
                if (hex.owner === gameState?.currentPlayer && !hex.minion && selectedMinionType) {
                    setShowSpawnConfirm(true)
                } else {
                    setShowSpawnConfirm(false)
                }
                setShowBuyConfirm(false)
            }
        },
        [selectedHex, setSelectedHex, gameState?.currentPlayer, selectedMinionType, setShowBuyConfirm, setShowSpawnConfirm]
    )

    const handleRequestBuyHex = useCallback(
        (hex: HexCoord) => {
            if (!hex) return
            setSelectedHex(hex)
            setShowBuyConfirm(true)
        },
        [setSelectedHex, setShowBuyConfirm]
    )

    const handleConfirmBuyHex = useCallback(async () => {
        if (!selectedHex) return

        try {
            await purchaseMutation.mutateAsync({
                col: selectedHex.col,
                row: selectedHex.row,
            })

            setHasBoughtHex(true)
            setShowBuyConfirm(false)

        } catch (err) {
            showErrorFn(shortenError(err))
            setSelectedHex(null)
            setShowBuyConfirm(false)
        }

        setSelectedHex(null)
    }, [selectedHex, purchaseMutation, showErrorFn, setSelectedHex, setShowBuyConfirm, setHasBoughtHex])
    const handleCancelBuyHex = useCallback(() => {
        setSelectedHex(null)
        setShowBuyConfirm(false)
    }, [setSelectedHex, setShowBuyConfirm])

    const handleSelectMinion = useCallback(
        (type: string) => {
            if (selectedMinionType === type) {
                setSelectedMinionType(null)
                setShowSpawnConfirm(false)
            } else {
                setSelectedMinionType(type)
                // If we already have a hex selected, show spawn confirm
                if (selectedHex && selectedHex.owner === gameState?.currentPlayer && !selectedHex.minion) {
                    setShowSpawnConfirm(true)
                }
            }
        },
        [selectedMinionType, setSelectedMinionType, selectedHex, gameState?.currentPlayer, setShowSpawnConfirm]
    )

    const handleConfirmSpawn = useCallback(async () => {
        if (!selectedHex || !selectedMinionType) return

        try {
            if (gameState?.phase === 'SETUP') {
                await initialSpawnMutation.mutateAsync({
                    typeName: selectedMinionType,
                    col: selectedHex.col,
                    row: selectedHex.row,
                })
            } else {
                await spawnMutation.mutateAsync({
                    typeName: selectedMinionType,
                    col: selectedHex.col,
                    row: selectedHex.row,
                })
            }
            setHasSpawnedMinion(true)
        } catch (err) {
            showErrorFn(shortenError(err))
        }

        setSelectedMinionType(null)
        setSelectedHex(null)
        setShowSpawnConfirm(false)
    }, [selectedHex, selectedMinionType, gameState?.phase, initialSpawnMutation, spawnMutation, showErrorFn, setSelectedMinionType, setSelectedHex, setShowSpawnConfirm, setHasSpawnedMinion])

    const handleCancelSpawn = useCallback(() => {
        setSelectedMinionType(null)
        setSelectedHex(null)
        setShowSpawnConfirm(false)
    }, [setSelectedMinionType, setSelectedHex, setShowSpawnConfirm])

    const handleEndTurn = useCallback(async () => {
        try {
            await nextTurnMutation.mutateAsync()
        } catch (err) {
            showErrorFn(shortenError(err))
        }
    }, [nextTurnMutation, showErrorFn])

    const handleEndGame = useCallback(async () => {
        try {
            await surrenderMutation.mutateAsync()
            localStorage.removeItem("gameMinions")
        } catch (err) {
            setError(shortenError(err))
        }
    }, [surrenderMutation, setError])

    const startAutoPlay = useCallback(async () => {
        try {
            await autoPlayMutation.mutateAsync({ action: 'start' })
            setIsAutoPlaying(true)
        } catch (err) {
            setError(shortenError(err))
        }
    }, [autoPlayMutation, setIsAutoPlaying, setError])

    const stopAutoPlay = useCallback(async () => {
        try {
            await autoPlayMutation.mutateAsync({ action: 'stop' })
            setIsAutoPlaying(false)
        } catch (err) {
            setError(shortenError(err))
        }
    }, [autoPlayMutation, setIsAutoPlaying, setError])

    return {
        gameState,
        loading,
        error,
        availableMinionTypes,
        currentPlayerBudget,
        selectedHex,
        adjacentHexes,
        hasBoughtHex,
        hasSpawnedMinion,
        showBuyConfirm,
        handleRequestBuyHex,
        handleConfirmBuyHex,
        handleCancelBuyHex,
        selectedMinionType,
        handleSelectMinion,
        showSpawnConfirm,
        handleConfirmSpawn,
        handleCancelSpawn,
        handleSelectHex,
        handleEndTurn,
        handleEndGame,
        refresh: refetch,
        isAutoPlaying,
        startAutoPlay,
        stopAutoPlay,
        showErrorModal,
        errorMessage,
        closeErrorModal,
    }
}

function determineWinner(dto: GameStateDTO): string {
    if (dto.gameOverReason) {
        if (dto.gameOverReason.includes('Player 2 wins')) return 'Player 2'
        if (dto.gameOverReason.includes('Player 1 wins')) return 'Player 1'
        if (dto.gameOverReason.includes('Player 2')) return 'Player 2'
        if (dto.gameOverReason.includes('Player 1')) return 'Player 1'
    }
    
    const p1Minions = dto.player1Minions?.length ?? 0
    const p2Minions = dto.player2Minions?.length ?? 0

    if (p1Minions === 0 && p2Minions > 0) return 'Player 2'
    if (p2Minions === 0 && p1Minions > 0) return 'Player 1'
    if (p1Minions > p2Minions) return 'Player 1'
    if (p2Minions > p1Minions) return 'Player 2'

    return 'Tie'
}
