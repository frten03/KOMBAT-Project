export { default as BackButton } from "../components/BackButton.tsx";
export { default as HexBoard } from "../components/HexBoard.tsx"
export { default as HexCell } from "../components/HexCell.tsx";
export { default as MinionPanel } from "../components/MinionPanel.tsx"
export { default as SidePanel } from "../components/SidePanel.tsx"
export { default as TopBar } from "../components/TopBar.tsx"

import archerImg from "./minions/archer.png"
import guardianImg from "./minions/guardian.png"
import warriorImg from "./minions/warrior.png"
import scoutImg from "./minions/scout.png"
import mageImg from "./minions/mage.png"

export const MinionsIMG = {
    Archer: archerImg,
    Guardian: guardianImg,
    Warrior: warriorImg,
    Scout: scoutImg,
    Mage: mageImg
}