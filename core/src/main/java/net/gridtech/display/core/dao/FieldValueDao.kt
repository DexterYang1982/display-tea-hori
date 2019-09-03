package net.gridtech.display.core.dao

import net.gridtech.core.data.IFieldValue
import net.gridtech.core.data.IFieldValueDao

class FieldValueDao : IFieldValueDao {
    private val map = HashMap<String, ArrayList<IFieldValue>>()
    override fun getAll(): List<IFieldValue> = map.values.map { it.first() }

    override fun getById(id: String): IFieldValue? = map[id]?.first()

    override fun save(d: IFieldValue) {
        val history = map.getOrPut(d.id) {
            ArrayList()
        }
        history.add(0, d)
    }

    override fun delete(id: String) {
        map.remove(id)
    }

    override fun getByNodeId(nodeId: String): List<IFieldValue> = getAll().filter { it.nodeId == nodeId }

    override fun getByFieldId(fieldId: String): List<IFieldValue> = getAll().filter { it.fieldId == fieldId }

    override fun getSince(id: String, since: Long): List<IFieldValue> =
        map[id]?.filter { it.updateTime > since } ?: emptyList()
}