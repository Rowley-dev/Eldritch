package org.rsmod.content.travel.spirittree.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias spirit_tree_npcs = SpiritTreeNpcs

object SpiritTreeNpcs : NpcReferences() {

    val spirit_tree_chathead_small = find("treevillage_small_spirittree")
    val spirit_tree_chathead_big = find("treevillage_spirittree")
    val spirit_tree_chathead_pog = find("pog_spirit_tree_healthy_dummy")
}
