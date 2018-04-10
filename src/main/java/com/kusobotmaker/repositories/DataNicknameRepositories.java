package com.kusobotmaker.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataNickname;

public interface DataNicknameRepositories extends JpaRepository<DataNickname, Long> {
	//DataNickname findFirstByBot_idAndFriends_id(Long Bot_id,Long Friends_id);
	DataNickname findTopByBotIdAndFriendsId(Long long1,Long long2);
}
