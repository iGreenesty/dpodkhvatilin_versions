def lastComment = sourceObject
notification.scriptParams['commentText'] = lastComment.text;

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';


def mentionUUIDs = mention.newMentions.findAll{ it.tokenize('$')[0] == 'employee' }
mentionUUIDs.each {
  uuid ->
  def empl = utils.get(uuid)
  notification.toEmployee << empl
}

//Исключает автора действий из получателей оповещения.
if(null != user) { 
  notification.to.remove(user.email)
}