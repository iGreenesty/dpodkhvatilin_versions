def comment = utils.comments(subject).last()
def author = comment.author
push.scriptParams['author'] = author?.title ?: 'Суперпользователь'

if(author) {
  push.toRemoveEmployee << author
}