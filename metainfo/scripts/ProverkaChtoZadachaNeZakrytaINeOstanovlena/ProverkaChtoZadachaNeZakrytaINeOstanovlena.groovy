if(subject.state == 'stopped') {
  return "Задача остановлена"
}
if(subject.state == 'closed') {
  return "Задача закрыта"
}
return ""