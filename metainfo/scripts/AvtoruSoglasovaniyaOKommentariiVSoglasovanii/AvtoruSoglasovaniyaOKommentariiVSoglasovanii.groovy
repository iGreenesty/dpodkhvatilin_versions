// Получаем автора комментария
def commentAuthor = sourceObject.author?.title ?: 'Суперпользователь'
notification.scriptParams['commentAuthor'] = commentAuthor

// Получаем текст комментария
def comment = sourceObject
notification.scriptParams['commentText'] = comment.text

// Исключаем автора комментария из списка оповещаемых
def author = comment.author
if(author != null) {
  notification.toEmployee.remove(author)
  notification.to.remove(author.email)
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''