package com.example.financeapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.financeapp.R
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.ChatRequest
import com.example.financeapp.model.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AIFragment : Fragment() {

    private lateinit var edtQuestion: EditText
    private lateinit var btnSend: Button
    private lateinit var txtResponse: TextView
    private lateinit var progressBar: ProgressBar

    // 👉 giả lập userId (sau này bạn lấy từ login)
    private val userId: Long = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_ai, container, false)

        edtQuestion = view.findViewById(R.id.edtQuestion)
        btnSend = view.findViewById(R.id.btnSend)
        txtResponse = view.findViewById(R.id.txtResponse)
        progressBar = view.findViewById(R.id.progressBar)

        btnSend.setOnClickListener {

            val question = edtQuestion.text.toString().trim()

            if (question.isEmpty()) {
                Toast.makeText(requireContext(), "Nhập câu hỏi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendToAI(question)
        }

        return view
    }

    private fun sendToAI(question: String) {

        progressBar.visibility = View.VISIBLE
        txtResponse.text = ""

        val request = ChatRequest(question)

        RetrofitClient.api.chatAI(userId, request)
            .enqueue(object : Callback<ChatResponse> {

                override fun onResponse(
                    call: Call<ChatResponse>,
                    response: Response<ChatResponse>
                ) {

                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {

                        val reply = response.body()?.reply

                        txtResponse.text = if (!reply.isNullOrEmpty()) {
                            reply
                        } else {
                            "Không có phản hồi"
                        }

                    } else {
                        txtResponse.text = "Lỗi server: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    txtResponse.text = "Lỗi kết nối: ${t.message}"
                }
            })
    }
}