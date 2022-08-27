package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
// Stub
public class PostRepository {

  private ConcurrentMap<Long, Post> repository = new ConcurrentHashMap<>();
  private AtomicLong counter = new AtomicLong(1);

  public List<Post> all() {
    List<Post> list = new ArrayList<Post>(repository.values());
    return list;
  }

  public Optional<Post> getById(long id) {
    return Optional.of(repository.get(id));
  }

  public Post save(Post post) {
    long postId = post.getId();
    if (postId == 0) {
      postId = counter.getAndIncrement();
      post.setId(postId);
    } else {
      Post element = repository.get(postId);
      if (element != null) {
        repository.replace(postId, post);
      } else {
        String exception = "Пост с id=" + postId + " не найден";
        throw new NotFoundException(exception);
      }
    }
    repository.putIfAbsent(post.getId(), post);
    return post;
  }

  public void removeById(long id) {
    repository.remove(id);
  }
}
