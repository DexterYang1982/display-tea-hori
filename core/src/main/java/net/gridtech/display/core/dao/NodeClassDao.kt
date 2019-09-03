package net.gridtech.display.core.dao

import net.gridtech.core.data.INodeClass
import net.gridtech.core.data.INodeClassDao

class NodeClassDao : INodeClassDao {
    private val map = HashMap<String, INodeClass>()
    override fun getAll(): List<INodeClass> = map.values.toList()
    override fun getById(id: String): INodeClass? = map[id]
    override fun save(d: INodeClass) {
        map[d.id] = d
    }

    override fun delete(id: String) {
        map.remove(id)
    }

    override fun getByTags(tags: List<String>): List<INodeClass> =
        map.values.filter { it.tags.containsAll(tags) }
}