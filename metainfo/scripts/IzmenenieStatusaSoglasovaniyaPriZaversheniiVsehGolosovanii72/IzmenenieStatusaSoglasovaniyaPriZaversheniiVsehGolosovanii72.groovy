//SCRIPTSD4000427
//Код: Смена статуса согласования при завершении всех голосований
//Назначение:
/**
* После того как все голосования завершены у связанного
согласования необходимо сменить статус по следующему правилу:
* - Если все голосования одобрены - статус согласования должен
стать "Согласовано"
* - Одно или более голосований отклонены - статус согласования
должен стать "Отклонено"
*/
//Версия: 4.0
//Категория: Статусы запроса, условие входа/выхода из статуса
//ПАРАМЕТРЫ-----------------------------------------------------

def VOTE_CODE = 'vote' // Код класса "Голосование"
def V_REFUSED = 'closed' // Код статуса "Отклонено" класса "Голосование"
def V_ACCEPTED = 'accepted' // Код статуса "Согласовано" класса "Голосование"
def V_REGISTERD = 'registered' // Код статуса "Не голосовал" класса "Голосование"

def N_ACCEPTED = 'accepted' // Код статуса "Согласовано" класса "Согласование"
def N_REFUSED = 'rejected' // Код статуса "Отклонено" класса "Согласование"
//ОСНОВНОЙ БЛОК-------------------------------------------------

//Проверка корректности значений параметров скрипта
if ((VOTE_CODE == null) || (V_REFUSED == null) || (V_ACCEPTED == null)	|| (V_REGISTERD == null) || (N_ACCEPTED == null) || (N_REFUSED == null)) {
  utils.throwReadableException('Необходимо заполнить параметры скрипта')
}

//Проверяем является ли голосование "последним"
def allVotes = (utils.find(VOTE_CODE, ['parent' : subject.parent, 'state' : V_REGISTERD]).findAll { it?.UUID != subject?.UUID }).size()
if (allVotes == 0) {
  if (subject.state == V_REFUSED) {
    utils.edit(subject.parent, ['state' : N_REFUSED])
  }
  else {
    def refusedVotes = utils.find(VOTE_CODE, ['parent' : subject.parent, 'state' : V_REFUSED])
    if (refusedVotes.isEmpty()) {
      utils.edit(subject.parent, ['state' : N_ACCEPTED])
    }
    else {
      utils.edit(subject.parent, ['state' : N_REFUSED])
    }
  }
}