def VOTE_OD_STATE = 'overdue' //код статуса Просрочен для Голосования
def VOTE_REG_STATE = 'registered' //код статуса На согласовании для Голосования
def NEG_OD_STATE = 'overdue' //код статуса Просрочен для Согласования
def NEG_VT_ATTR = 'votes' //код атрибута Голосования класса Согласование

def votesToOverdue = subject.votes.findAll{it -> it.state == VOTE_REG_STATE}
votesToOverdue.each {
  	it -> utils.edit(it, ['state' : VOTE_OD_STATE])
}
utils.edit(subject, ['state' : NEG_OD_STATE])