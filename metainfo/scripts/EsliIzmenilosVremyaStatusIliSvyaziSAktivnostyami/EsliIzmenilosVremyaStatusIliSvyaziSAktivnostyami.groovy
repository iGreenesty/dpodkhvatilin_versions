def ATTRS_TO_CHECK = ['serviceCall', 'task', 'changeRequest', 'problem', 'time', 'state']
return (ATTRS_TO_CHECK.disjoint(changedAttributes)) ? 'Время и связи не изменились' : ''