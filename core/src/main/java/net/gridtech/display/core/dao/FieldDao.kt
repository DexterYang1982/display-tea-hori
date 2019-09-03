package net.gridtech.display.core.dao

import net.gridtech.core.data.IField
import net.gridtech.core.data.IFieldDao

class FieldDao : IFieldDao {
    private val map = HashMap<String, IField>()
    override fun getAll(): List<IField> = map.values.toList()

    override fun getById(id: String): IField? = map[id]

    override fun save(d: IField) {
        map[d.id] = d
    }

    override fun delete(id: String) {
        map.remove(id)
    }

    override fun getByNodeClassId(nodeClassId: String): List<IField> =
        map.values.filter { it.nodeClassId == nodeClassId }

}