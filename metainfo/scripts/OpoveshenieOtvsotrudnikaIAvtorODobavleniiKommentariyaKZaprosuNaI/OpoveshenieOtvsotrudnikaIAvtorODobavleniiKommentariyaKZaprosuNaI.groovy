// Получаем автора комментария
def commentAuthor = sourceObject.author?.title ?: 'Суперпользователь'
notification.scriptParams['commentAuthor'] = commentAuthor

// Получение текста последнего комментария
def lastComment = sourceObject
notification.scriptParams['commentText'] = lastComment.text;

// Исключение автора комментария действия из получателей
def author = lastComment.author
if(author != null) {
  notification.toEmployee.remove(author)
  notification.to.remove(author.email)
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''                  