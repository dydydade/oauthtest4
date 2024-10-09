package login.tikichat.domain.bookmark.service;

import login.tikichat.domain.bookmark.model.Bookmark;
import login.tikichat.domain.bookmark.repository.BookmarkRepository;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Service
// @Slf4j
// @RequiredArgsConstructor
// public class BookmarkService {

//     private final ChatRoomRepository chatRoomRepository;
//     private final UserRepository userRepository;
//     private final BookmarkRepository bookmarkRepository;

//     @Transactional
//     public void saveBookmark(Long userId, Long chatRoomId) {
//         final var user = this.userRepository.findById(userId).orElseThrow(
//                 () -> new BusinessException(ErrorCode.NOT_FOUND_USER)
//         );
//         final var chatRoom = this.chatRoomRepository.findById(chatRoomId).orElseThrow(
//                 () -> new BusinessException(ErrorCode.NOT_FOUND_CHAT_ROOM)
//         );

//         boolean isBookmarkedAlready = this.bookmarkRepository.existsByUserIdAndChatroomId(userId, chatRoomId);
//         if (isBookmarkedAlready) throw new BusinessException(ErrorCode.ALREADY_BOOKMARKED_CHATROOM);

//         final var bookmark = new Bookmark(user, chatRoom);

//         this.bookmarkRepository.save(bookmark);
//     }

//     @Transactional
//     public void deleteBookmark(Long userId, Long chatRoomId) {
//         final var user = this.userRepository.findById(userId).orElseThrow(
//                 () -> new BusinessException(ErrorCode.NOT_FOUND_USER)
//         );
//         final var chatRoom = this.chatRoomRepository.findById(chatRoomId).orElseThrow(
//                 () -> new BusinessException(ErrorCode.NOT_FOUND_CHAT_ROOM)
//         );
//         final var bookmark = this.bookmarkRepository.findByUserIdAndChatroomId(userId, chatRoomId).orElseThrow(
//                 () -> new BusinessException(ErrorCode.NOT_FOUND_BOOKMARK)
//         );

//         this.bookmarkRepository.delete(bookmark);
//     }
// }
