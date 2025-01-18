require: functions.js
require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Этот бот служит для нахождения доступных вакансий для соискателя. Чем могу вам помочь?

    state: Hello
        intent!: /привет
        a: Здраствуйте!

    state: Bye
        intent!: /пока
        a: До свидания! Надеюсь смог вам помочь.
        
    state: AskJobTitle
        intent!: /запрос_профессии
        a: Укажите профессию, например: программист, разработчик, инженер.
        # slot: jobTitle
        
    state: AskLocation
        intent!: /запрос_региона
        a: Укажите регион, в котором вы ищете вакансию, например: Республика Бурятия, Московская область.
        #slot: location
        
    state: AskEmployment
        intent!: /запрос_типа_занятости
        a: Укажите желаемый тип занятости, например, полная занятость, Временная и т.д.
        #slot: employment
        
    state: AskSalary
        intent!: /запрос_минимальной_заработной_платы
        random:
            a: Укажите минимальный размер заработной платы
            a: На какую минимальную зарплату вы готовы согласиться?
        #slot: salary
        
    
    state: HandleApiResponse
        intent!: /поиск
        script:
            # Выполняем запрос через API для поиска вакансий
            fetchVacancies().then(function (res) {
                if (res.status === '200' && res.results.vacancies.length > 0) {
                    $session.vacancies = res.results.vacancies;
                    $session.page = 0;
                    $reactions.answer("Вакансии успешно найдены! Перехожу к отображению...");
                    showPage($session.page, $session.vacancies);
                } else {
                    $reactions.answer("К сожалению, вакансии по вашему запросу не найдены.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
            });
    
        state: NextPage
            q: *след*
            script:
                if ($session.page !== undefined && $session.vacancies) {
                    $session.page++;
                    if ($session.page * 3 < $session.vacancies.length) {
                        showPage($session.page, $session.vacancies);
                    } else {
                        $reactions.answer("Это последняя страница.");
                    }
                } else {
                    $reactions.answer("Сначала выполните поиск вакансий.");
                }
                
        state: PrevPage
            q: *пред*
            script:
                if ($session.page > 0) {
                    $session.page--;
                    showPage($session.page, $session.vacancies);
                } else {
                    $reactions.answer("Это первая страница.");
                }

    state: NoMatch
        event!: noMatch
        random:
            a: Извините, я не понимаю. Переформулируйте, пожалуйста
            a: Простите, кажется я вас не понял. Напишите своё предложение иначе.
            a: К сожалению я не понял, что значит следующее сообщение: {{$request.query}}
        
    state: Profanity
        q!: * @mystem.obsc *
        a: Пожалуйста, избегайте нецензурных выражений. Мы стараемся поддерживать уважительную атмосферу.

    state: ProfanityAlt
        q!: * @mlps-obscene.obscene *
        a: Ваше сообщение содержит нецензурные слова. Пожалуйста, используйте более корректные выражения.