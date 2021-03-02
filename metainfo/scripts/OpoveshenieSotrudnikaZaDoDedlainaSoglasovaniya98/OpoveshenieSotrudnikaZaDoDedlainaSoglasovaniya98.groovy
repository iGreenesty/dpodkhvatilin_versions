// Добавление в получатели оповещения согласующих лиц, 
// голосования которых находятся в статусе На согласовании
subject.votes.each {
  if(it.state == 'registered') {
  	notification.toEmployee << it.voter_em
  }
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

// Подпись к оповещению
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''

// Определение описания источника
notification.scriptParams['sourceDescr'] = subject.source?.description ?: subject.sourceR?.descriptionRTF ?: '[не указано]'