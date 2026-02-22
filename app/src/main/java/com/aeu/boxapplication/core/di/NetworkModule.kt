import com.aeu.boxapplication.data.remote.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://subscription-backend-528466251837.us-central1.run.app/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Expose the API service so MainActivity/Repository can use it
    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}