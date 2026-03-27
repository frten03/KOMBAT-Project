import "../styles/minionpanel.css"
import type {MinionConfig} from "../types/gameTypes"
import { MinionsIMG } from "../assets"

interface MinionPanelProps {
    minions : MinionConfig[]
    onSelect: (m: MinionConfig) => void
    selected: MinionConfig | null
}

export default function MinionPanel({minions = [], onSelect, selected}: MinionPanelProps) {
    const getAvatar = (type: string): string | undefined => {
        return (MinionsIMG as Record<string, string>)[type]
    }

    return (
        <div className="minion-panel">
            <div className="minion-grid-container">
            {minions.map((minion, i) => (
                <div
                    key={i}
                    className={`minion-slot ${
                        selected?.type === minion.type ? "selected" : ""
                    }`}
                    onClick={() => onSelect(minion)}
                >
                    <img
                        src={getAvatar(minion.type)}
                        alt={minion.customName}
                        className="minion-avatar"
                    />
                    <div className="minion-name">
                        {minion.customName}
                    </div>
                </div>
            ))}</div>

            <div className="spawn-cost">
                spawn cost: 100 $
            </div>

        </div>
    )
}