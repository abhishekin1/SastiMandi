package abhishekg.pkart.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product : ProductModel)

    @Delete
    suspend fun deleteProduct(product: ProductModel)

    @Query("SELECT * FROM products")
    fun getAllProducts() : LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun isExit(id : String) : ProductModel

    @Query("UPDATE products set productQuan= :quan  WHERE productId = :id")
    fun updateQuan(id : String, quan: String)

    @Query("SELECT productQuan FROM products WHERE productId = :id")
    fun getQuan(id : String) : String

    @Query("SELECT COUNT(*) FROM products")
    fun getSize() : String





}