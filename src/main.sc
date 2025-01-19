require: functions.js
require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        random:
            a: Здраствуйте! Этот бот служит для нахождения доступных вакансий. Чем могу вам помочь?
            a: Приветсвую, я бот для поиска вакансии. Как я могу вам помочь?
        buttons:
            "Заполнить анкету" -> ./Survey
            "Поиск" -> ./HandleApiResponse

    state: Hello
        intent!: /привет
        a: Ещё раз здраствуйте)



    state: Survey
        intent!: /анкета
        random:
            a: Отлично! Давайте заполним небольшую анкету
            a: Хорошо, заполним анкету для просмотра вакансий
            a: Замечательно! Приступим к заполнению анкеты
        script:
            // Инициализируем анкету в сессии, если она ещё не создана
            if (!$session.survey) {
                $session.survey = {
                    jobTitle: null,
                    region: null,
                    city: null,
                    employment: null,
                    schedule: null,
                    salary: null
                };
            }
    
            // Проверяем, какие данные ещё не заполнены
            if (!$session.survey.jobTitle) {
                $reactions.answer("Какую профессию вы ищете? Например: программист, инженер");
                $reactions.transition("/Survey/AwaitJobTitle");
            } else if (!$session.survey.region) {
                $reactions.answer("В каком регионе вы ищете работу? Например:  Республика Бурятия, Московская область");
                $reactions.transition = "AwaitRegion";
            } else if (!$session.survey.region) {
                $reactions.answer("В каком городе вы ищете работу? Например:  Улан-Удэ, Москва");
                $reactions.transition = "AwaitCity";
            } else if (!$session.survey.employment) {
                $reactions.answer("Какой тип занятости вас интересует? Например: полная занятость, временная, частичная.");
                $reactions.transition = "AwaitEmployment";
            } else if (!$session.survey.salary) {
                $reactions.answer("Какой график вам подходит? Например: Полный рабочий день, Сменная работа, Вахтовый метод, Режим гибкого рабочего времени, Неполный рабочий день/неполная рабочая неделя.");
                $reactions.transition = "AwaitSchedule";
            } else if (!$session.survey.schedule) {
                $reactions.answer("Какую минимальную зарплату вы ожидаете? Укажите сумму в рублях.");
                $reactions.transition = "AwaitSalary";
            }
            
        state: AwaitJobTitle
            a: БЛЯТЬ!
            #q: программист|инженер|разработчик|учитель|менеджер
            #script:
                #var jobTitle = $request.query.toLowerCase().trim();
                #$session.survey.jobTitle = jobTitle;
                #$reactions.transition("Survey");
    
        state: AwaitRegion
            #q: 03|бурятия|инженер
            script:
                var region = $request.query.toLowerCase().trim();
                $session.survey.region = region;
                $reactions.transition("Survey");
            
        state: AwaitCity
            #q: москва|питер
            script:
                var city = $request.query.toLowerCase().trim();
                $session.survey.city = city;
                $reactions.transition("Survey");
            
        state: AwaitEmployment
            #q: полная|временная|частичная
            script:
                var employment = $request.query.toLowerCase().trim();
                $session.survey.employment = employment;
                $reactions.transition("Survey");
            
        state: AwaitSchedule
            #q: полный|сменная
            script:
                var schedule = $request.query.toLowerCase().trim();
                $session.survey.schedule = schedule;
                $reactions.transition("Survey");
    
        state: AwaitSalary
            q: * @duckling.number *
            script:
                    $session.survey.salary = salary;
                    $reactions.transition("Survey");
    
        state: SurveyComplete
            script:
                $reactions.answer("Спасибо! Ваша анкета заполнена.");
                $reactions.answer("Профессия: " + $session.survey.jobTitle);
                $reactions.answer("Регион: " + $session.survey.region);
                $reactions.answer("Город: " + $session.survey.city);
                $reactions.answer("Тип занятости: " + $session.survey.employment);
                $reactions.answer("График: " + $session.survey.schedule);
                $reactions.answer("Минимальная зарплата: " + $session.survey.salary + " руб.");


    
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
               
               
                
                
    state: Bye
        intent!: /пока
        random:
            a: Всего доброго! Рад был помочь)
            a: До свидания! Надеюсь смог вам помочь
            a: Удачи! Рад что был вам полезен)



    state: NoMatch
        event!: noMatch
        random:
            a: Извините, я не понимаю. Переформулируйте, пожалуйста
            a: Простите, кажется я вас не понял. Напишите своё предложение иначе.
            a: К сожалению я не понял, что значит следующее сообщение: {{$request.query}}



    state: Profanity
        q!: * @mystem.obsc *
        random:
            a: Ваше сообщение содержит нецензурные слова. Пожалуйста, используйте более корректные выражения.
            a: Пожалуйста, избегайте нецензурных выражений. Мы стараемся поддерживать уважительную атмосферу.
            a: Давайте соблюдать нормы приличия и не использовать подобные выражения.

    state: ProfanityAlt
        q!: * @mlps-obscene.obscene *
        random:
            a: Ваше сообщение содержит нецензурные слова. Пожалуйста, используйте более корректные выражения.
            a: Пожалуйста, избегайте нецензурных выражений. Мы стараемся поддерживать уважительную атмосферу.
            a: Давайте соблюдать нормы приличия и не использовать подобные выражения.
