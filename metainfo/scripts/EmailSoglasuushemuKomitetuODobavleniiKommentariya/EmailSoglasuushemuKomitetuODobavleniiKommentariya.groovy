// Получаем автора комментария
def commentAuthor = sourceObject.author?.title ?: 'Суперпользователь'
notification.scriptParams['commentAuthor'] = commentAuthor

// Получаем текст комментария
def comment = sourceObject
notification.scriptParams['commentText'] = comment.text

// Определяем список получателей
def allVotes = subject.votes
allVotes.each {
  notification.toEmployee << it.voter_em
}

// Исключаем автора комментария из списка оповещаемых
def author = comment.author
if(author != null) {
  notification.toEmployee.remove(author)
  notification.to.remove(author.email)
}

notification.scriptParams['getVote'] = {
  empl ->
  def vote = subject.votes.find{ it.voter_em?.UUID == empl?.UUID }
  return vote
}

notification.scriptParams['number'] = {
  empl ->
  def vote = subject.votes.find{ it.voter_em?.UUID == empl?.UUID }
  return vote.title
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''