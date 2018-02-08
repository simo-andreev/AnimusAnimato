package sim.o.bg.animusanimato.model

import java.net.URL

open class BaseDataObject(_id: Long)

data class Client(val name: String, val _id: Long, val photoURL: URL) : BaseDataObject(_id) {
    val description: String = "Description for $name with id:[$_id]"
}