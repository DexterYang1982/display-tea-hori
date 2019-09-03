package net.gridtech.display.core.dao

import net.gridtech.core.data.INode
import net.gridtech.core.data.INodeDao

class NodeDao : INodeDao {
    private val map = HashMap<String, INode>()
    override fun getAll(): List<INode> = map.values.toList()

    override fun getById(id: String): INode? = map[id]
    override fun save(d: INode) {
        map[d.id] = d
    }

    override fun delete(id: String) {
        map.remove(id)
    }

    override fun getByNodeClassId(nodeClassId: String): List<INode> =
        map.values.filter { it.nodeClassId == nodeClassId }

    override fun getByBranchNodeId(branchNodeId: String): List<INode> =
        map.values.filter { it.path.contains(branchNodeId) || it.id == branchNodeId }
}