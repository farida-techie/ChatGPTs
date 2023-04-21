package com.malkinfo.chatgpts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(
    var messageList :List<Message>
):RecyclerView.Adapter<MessageAdapter.MesgViewHolder>()
{
    inner class MesgViewHolder(var v: View):RecyclerView.ViewHolder(v){
        val leftChatView = v.findViewById<LinearLayout>(R.id.left_chat_view)
        val leftTextView = v.findViewById<TextView>(R.id.left_chat_text_view)
        val rightChatView = v.findViewById<LinearLayout>(R.id.right_chat_view)
        val rightTextView = v.findViewById<TextView>(R.id.right_chat_text_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesgViewHolder {
       val chatView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item,parent,false)
        return MesgViewHolder(chatView)
    }

    override fun getItemCount(): Int =messageList.size

    override fun onBindViewHolder(holder: MesgViewHolder, position: Int) {
        val message = messageList[position]
        if (message.sentBy == Message.SENT_BY_ME){
            holder.leftChatView.visibility = View.GONE
            holder.rightChatView.visibility = View.VISIBLE
            holder.rightTextView.text = message.message
        }else{
            holder.leftChatView.visibility = View.VISIBLE
            holder.rightChatView.visibility = View.GONE
            holder.leftTextView.text = message.message
        }

    }
}