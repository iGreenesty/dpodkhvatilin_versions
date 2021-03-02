//создание согласования при переходе ЗНИ в статус "На согласовании"

//SCRIPTSD4000145
// скрипт копирования атрибутов системный объект - пользовательский объект
//создает пользовательский объект Запрос с предзаполненными полями из системного

//куда копируем
if (subject.advisoryEmpl != null) {
  CAB = 'cabEmployee' // код атрибута "Согласующий комитет (сотрудники)".
  SERVICE_CALL = 'source' // код атрибута "Связанный объект класса обращение".
  WORKS_FQN = 'negotiation$negotiation' // идентификатор типа создаваемого объекта
  DATE_NEG = 'period' // код атрибута "Период согласования"
  AUTHOR = 'author' // код атрибута "Автор"


//ОСНОВНОЙ БЛОК--------------------------------------------------------
//откуда копируем

  def attrs = [:]
  attrs[CAB]= subject.advisoryEmpl
  attrs[SERVICE_CALL]= subject
  attrs[DATE_NEG] = subject.periodNegotiat
  attrs['@user'] = user
  attrs[AUTHOR] = user
  utils.create(WORKS_FQN, attrs)
}