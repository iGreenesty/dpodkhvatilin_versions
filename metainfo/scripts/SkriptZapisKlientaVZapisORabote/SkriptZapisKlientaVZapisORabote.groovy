def TASK_ATTR = 'task'
def SC_ATTR = 'serviceCall'
def TASK_SC = 'serviceCall'
def EMP = 'clientEmployee'
def OU = 'clientOU'
def WORK_EMP = 'client_em'
def WORK_OU = 'client_ou'
def attr = [:]
if(subject[SC_ATTR]) {
	attr[WORK_EMP] = subject[SC_ATTR][EMP]
	attr[WORK_OU] = subject[SC_ATTR][OU]
}
else if((subject[TASK_ATTR]) && (subject[TASK_ATTR][TASK_SC])){
	attr[WORK_EMP] = subject[TASK_ATTR][TASK_SC][EMP]
	attr[WORK_OU] = subject[TASK_ATTR][TASK_SC][OU]
}
utils.edit(subject,attr);