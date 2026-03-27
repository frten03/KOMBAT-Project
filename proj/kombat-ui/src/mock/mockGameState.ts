import {MinionsIMG} from "../assets";
import type { MinionConfig } from "../types/gameTypes";

const rows = 8
const cols = 8

const mockBoard = Array.from({ length: rows * cols }).map((_, i) => {
    const r = Math.floor(i / cols) + 1
    const c = (i % cols) + 1

    let owner: number | null = null

    if ((r === 1 && c <= 3) || (r === 2 && c <= 2)) {
        owner = 1
    }

    if ((r === 8 && c >= 6) || (r === 7 && c >= 7)) {
        owner = 2
    }

    return {
        row: r,
        col: c,
        owner,
        minion: null
    }
})

const mockMinions: MinionConfig[] = [
    {
        type: "Archer",
        customName: "Swift Arrow",
        def: 80,
        strategy: "Ranged",
        isValid: true,
        avatar: MinionsIMG.Archer
    },
    {
        type: "Pou",
        customName: "Pou",
        def: 80,
        strategy: "Ranged",
        isValid: true,
        avatar: MinionsIMG.Pou
    },
    {
        type: "Tank",
        customName: "Tank",
        def: 80,
        strategy: "Ranged",
        isValid: true,
        avatar: MinionsIMG.Tank
    },
    {
        type: "Warrior",
        customName: "Warrior",
        def: 80,
        strategy: "Ranged",
        isValid: true,
        avatar: MinionsIMG.Warrior
    },
    {
        type: "Dog",
        customName: "Dog",
        def: 80,
        strategy: "Ranged",
        isValid: true,
        avatar: MinionsIMG.Dog
    }
]

export { mockBoard, mockMinions }