package com.zhangyt.module.home.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhangyt.module.home.R
import com.zhangyt.module.home.model.ChatItem
import com.zhangyt.utils.DateUtils

/**
 * 消息列表适配器。使用 BRVAH（BaseRecyclerViewAdapterHelper）。
 */
class ChatListAdapter : BaseQuickAdapter<ChatItem, BaseViewHolder>(R.layout.home_item_chat) {

    override fun convert(holder: BaseViewHolder, item: ChatItem) {
        holder.setText(R.id.tvNickname, item.nickname)
        holder.setText(R.id.tvLastMessage, item.lastMessage)
        holder.setText(R.id.tvTime, DateUtils.friendlyTime(item.timestamp))
        if (item.unreadCount > 0) {
            holder.setGone(R.id.tvUnread, false)
            holder.setText(R.id.tvUnread, item.unreadCount.toString())
        } else {
            holder.setGone(R.id.tvUnread, true)
        }
    }
}
