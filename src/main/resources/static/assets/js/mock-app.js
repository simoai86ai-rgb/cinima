
const MOVIES = [{"id": "neon-horizon", "title": "Neon Horizon", "year": 2026, "genre": "Sci-Fi", "runtime": "2h 18m", "rating": "8.7", "tag": "Featured", "desc": "A rescue pilot crosses a forbidden orbital boundary and discovers a signal that could rewrite humanity\u2019s future.", "c1": "#6d5dfc", "c2": "#13d8ff"}, {"id": "atlas-protocol", "title": "Atlas Protocol", "year": 2026, "genre": "Thriller", "runtime": "1h 56m", "rating": "8.4", "tag": "New", "desc": "A systems analyst uncovers a global contingency protocol designed to activate without human approval.", "c1": "#fb7185", "c2": "#7c3aed"}, {"id": "desert-echo", "title": "Desert Echo", "year": 2025, "genre": "Drama", "runtime": "2h 04m", "rating": "8.2", "tag": "Awarded", "desc": "A photographer returns to a desert town where every familiar place seems to hold a different version of the past.", "c1": "#f59e0b", "c2": "#ef4444"}, {"id": "black-tide", "title": "Black Tide", "year": 2026, "genre": "Action", "runtime": "2h 11m", "rating": "8.1", "tag": "Popular", "desc": "A salvage crew races a private militia to recover a mysterious cargo from a storm-battered research ship.", "c1": "#0f172a", "c2": "#0ea5e9"}, {"id": "paper-moon", "title": "Paper Moon", "year": 2025, "genre": "Romance", "runtime": "1h 48m", "rating": "7.9", "tag": "Editor Pick", "desc": "Two strangers exchange handwritten notes through a bookshop, never realizing they already meet every morning.", "c1": "#ec4899", "c2": "#fbbf24"}, {"id": "signal-zero", "title": "Signal Zero", "year": 2026, "genre": "Mystery", "runtime": "1h 51m", "rating": "8.5", "tag": "Trending", "desc": "A late-night radio host receives calls from a town that disappeared from every map thirty years ago.", "c1": "#22c55e", "c2": "#06b6d4"}, {"id": "northbound", "title": "Northbound", "year": 2024, "genre": "Adventure", "runtime": "2h 09m", "rating": "7.8", "tag": "Classic", "desc": "Four friends follow an abandoned railway into the frozen north in search of a station no train has reached in decades.", "c1": "#14b8a6", "c2": "#2563eb"}, {"id": "the-last-orbit", "title": "The Last Orbit", "year": 2026, "genre": "Sci-Fi", "runtime": "2h 27m", "rating": "9.0", "tag": "Top Rated", "desc": "The final crew aboard a failing space station must decide whether to return home or complete a mission no one remembers authorizing.", "c1": "#a855f7", "c2": "#2563eb"}, {"id": "hidden-frequency", "title": "Hidden Frequency", "year": 2025, "genre": "Crime", "runtime": "1h 44m", "rating": "7.7", "tag": "New", "desc": "A forensic sound engineer hears a pattern inside a routine recording that links a series of impossible crimes.", "c1": "#475569", "c2": "#d946ef"}, {"id": "after-rain", "title": "After Rain", "year": 2025, "genre": "Drama", "runtime": "1h 39m", "rating": "8.0", "tag": "Quiet Gem", "desc": "Following a flood, three generations rebuild a family home while confronting what they hoped the water had washed away.", "c1": "#38bdf8", "c2": "#64748b"}, {"id": "velocity", "title": "Velocity", "year": 2026, "genre": "Action", "runtime": "1h 58m", "rating": "8.3", "tag": "Popular", "desc": "A test driver trapped inside an autonomous prototype has ninety minutes to outsmart the network controlling the city.", "c1": "#f97316", "c2": "#ef4444"}, {"id": "small-worlds", "title": "Small Worlds", "year": 2024, "genre": "Animation", "runtime": "1h 32m", "rating": "8.6", "tag": "Family", "desc": "A curious child discovers a miniature civilization living between the plants on an apartment balcony.", "c1": "#84cc16", "c2": "#14b8a6"}];
const $=(q,s=document)=>s.querySelector(q), $$=(q,s=document)=>[...s.querySelectorAll(q)];
function icon(name,size=18){const paths={search:'<circle cx="11" cy="11" r="7"></circle><path d="m20 20-3.5-3.5"></path>',menu:'<path d="M4 7h16M4 12h16M4 17h16"></path>',play:'<path d="m9 7 8 5-8 5V7z"></path>',star:'<path d="m12 3 2.7 5.5 6 .9-4.35 4.24 1.03 5.98L12 16.8l-5.38 2.82 1.03-5.98L3.3 9.4l6-.9L12 3z"></path>',chev:'<path d="m9 18 6-6-6-6"></path>',home:'<path d="m3 11 9-8 9 8v9h-6v-6H9v6H3z"></path>',grid:'<rect x="4" y="4" width="6" height="6" rx="1"></rect><rect x="14" y="4" width="6" height="6" rx="1"></rect><rect x="4" y="14" width="6" height="6" rx="1"></rect><rect x="14" y="14" width="6" height="6" rx="1"></rect>',user:'<circle cx="12" cy="8" r="4"></circle><path d="M4 21c.8-4 3.4-6 8-6s7.2 2 8 6"></path>',plus:'<path d="M12 5v14M5 12h14"></path>',trash:'<path d="M4 7h16M9 7V4h6v3m-8 0 1 13h8l1-13"></path>'};return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${paths[name]||''}</svg>`}
function qs(k){return new URLSearchParams(location.search).get(k)}
function movieById(id){return MOVIES.find(m=>m.id===id)||MOVIES[0]}
function card(m,prefix=''){return `<article class="movie-card" data-title="${m.title.toLowerCase()}" data-genre="${m.genre}"><a href="${prefix}movie.html?id=${m.id}" class="poster-wrap"><img loading="lazy" src="${prefix}assets/img/posters/${m.id}.svg" alt="${m.title} poster"><span class="badge">${m.tag}</span><span class="poster-overlay"><span class="play-circle">${icon('play',22)}</span></span></a><div class="movie-info"><h3><a href="${prefix}movie.html?id=${m.id}">${m.title}</a></h3><div class="movie-sub"><span>${m.year} · ${m.genre}</span><span class="star">★ ${m.rating}</span></div></div></article>`}
function renderMovies(sel,items=MOVIES,limit=0,prefix=''){const el=$(sel);if(!el)return;el.innerHTML=(limit?items.slice(0,limit):items).map(m=>card(m,prefix)).join('')}
function setActiveNav(){const page=location.pathname.split('/').pop()||'index.html';$$('.nav a,.mobile-drawer a,.mobile-tabs a').forEach(a=>{const href=(a.getAttribute('href')||'').split('?')[0];if(href===page)a.classList.add('active')})}
function toast(msg){let t=$('.toast');if(!t){t=document.createElement('div');t.className='toast';document.body.appendChild(t)}t.textContent=msg;requestAnimationFrame(()=>t.classList.add('show'));setTimeout(()=>t.classList.remove('show'),2400)}
function initGlobal(){window.addEventListener('scroll',()=>$('.header')?.classList.toggle('scrolled',scrollY>12));$('.hamburger')?.addEventListener('click',()=>{$('.mobile-drawer')?.classList.toggle('open');document.body.classList.toggle('menu-open')});$$('[data-toast]').forEach(b=>b.addEventListener('click',e=>{e.preventDefault();toast(b.dataset.toast)}));$$('[data-lang]').forEach(b=>b.addEventListener('click',()=>{const current=document.documentElement.lang;const next=current==='en'?'fr':current==='fr'?'ar':'en';document.documentElement.lang=next;document.documentElement.dir=next==='ar'?'rtl':'ltr';$$('[data-lang]').forEach(x=>x.innerHTML=`${next.toUpperCase()} <span style="opacity:.6">⌄</span>`);toast(next==='ar'?'تم تفعيل اتجاه العربية للعرض التجريبي':next==='fr'?'Interface basculée en français (démo)':'English interface enabled')}));setActiveNav()}
function initHome(){renderMovies('#trending',MOVIES,6);renderMovies('#latest',[...MOVIES].reverse(),6);renderMovies('#recommended',MOVIES.slice(2).concat(MOVIES.slice(0,2)),6)}
function initMovies(){renderMovies('#allMovies');const input=$('#movieSearch'),genre=$('#genreFilter'),year=$('#yearFilter');function apply(){const q=(input?.value||'').toLowerCase(),g=genre?.value||'',y=year?.value||'';const arr=MOVIES.filter(m=>(!q||m.title.toLowerCase().includes(q)||m.genre.toLowerCase().includes(q))&&(!g||m.genre===g)&&(!y||String(m.year)===y));renderMovies('#allMovies',arr);$('#resultsCount').textContent=`${arr.length} titles`;if(!arr.length)$('#allMovies').innerHTML='<div class="panel" style="grid-column:1/-1"><h3>No matches found</h3><p class="muted">Try a different title, genre or year.</p></div>'}input?.addEventListener('input',apply);genre?.addEventListener('change',apply);year?.addEventListener('change',apply)}
function initMovie(){const m=movieById(qs('id'));const hero=$('#detailsHero');if(hero)hero.style.backgroundImage=`url(assets/img/backdrops/${m.id}.svg)`;$$('[data-movie-title]').forEach(x=>x.textContent=m.title);$$('[data-movie-desc]').forEach(x=>x.textContent=m.desc);$$('[data-movie-year]').forEach(x=>x.textContent=m.year);$$('[data-movie-genre]').forEach(x=>x.textContent=m.genre);$$('[data-movie-runtime]').forEach(x=>x.textContent=m.runtime);$$('[data-movie-rating]').forEach(x=>x.textContent=m.rating);const p=$('#detailsPoster');if(p){p.src=`assets/img/posters/${m.id}.svg`;p.alt=`${m.title} poster`}const w=$('#watchBtn');if(w)w.href=`watch.html?id=${m.id}`;renderMovies('#relatedMovies',MOVIES.filter(x=>x.id!==m.id&&x.genre===m.genre).concat(MOVIES.filter(x=>x.id!==m.id&&x.genre!==m.genre)),6)}
function initSearch(){const input=$('#globalSearch'),out=$('#searchResults'),count=$('#searchCount');let q=qs('q')||'';if(input)input.value=q;function draw(){q=(input?.value||'').trim();const arr=q?MOVIES.filter(m=>`${m.title} ${m.genre} ${m.desc}`.toLowerCase().includes(q.toLowerCase())):MOVIES.slice(0,8);if(count)count.textContent=q?`${arr.length} results for “${q}”`:'Popular searches';if(out)out.innerHTML=arr.map(m=>card(m)).join('')}draw();input?.addEventListener('input',draw)}
function initAdmin(){renderAdminRows();$$('.tab').forEach(t=>t.addEventListener('click',()=>{$$('.tab').forEach(x=>x.classList.remove('active'));t.classList.add('active');toast(`${t.textContent.trim()} language selected`)}));$('#adminSearch')?.addEventListener('input',e=>renderAdminRows(e.target.value));}
function renderAdminRows(q=''){const body=$('#adminMovieRows');if(!body)return;const list=MOVIES.filter(m=>m.title.toLowerCase().includes(q.toLowerCase()));body.innerHTML=list.map((m,i)=>`<tr><td><input class="checkbox" type="checkbox"></td><td><div class="table-movie"><img src="../assets/img/posters/${m.id}.svg" alt=""><div><strong>${m.title}</strong><div class="muted">${m.genre}</div></div></div></td><td>${m.year}</td><td><span class="status ${i%4===2?'draft':'published'}">${i%4===2?'Draft':'Published'}</span></td><td>${m.rating}</td><td><a class="btn btn-soft btn-sm" href="movie-form.html?id=${m.id}">Edit</a></td></tr>`).join('')}
document.addEventListener('DOMContentLoaded',()=>{initGlobal();const p=document.body.dataset.page;if(p==='home')initHome();if(p==='movies')initMovies();if(p==='movie')initMovie();if(p==='search')initSearch();if(p==='admin')initAdmin();})


// ===== V2: latest-movies hero slider + subtle motion =====
function initHeroSlider(){
  const root=$('#heroSlider');
  if(!root)return;
  const ids=['neon-horizon','atlas-protocol','the-last-orbit','velocity','signal-zero'];
  const items=ids.map(movieById);
  const stage=$('#heroStage'),dots=$('#heroDots'),progress=$('#heroProgress'),content=$('#heroContent');
  let current=0,timer=null,touchStartX=0,paused=false;
  stage.innerHTML=items.map((m,i)=>`<div class="hero-slide-bg ${i===0?'active':''}" style="background-image:url('assets/img/backdrops/${m.id}.svg')"></div>`).join('');
  dots.innerHTML=items.map((m,i)=>`<button class="hero-dot ${i===0?'active':''}" type="button" aria-label="Show ${m.title}" aria-pressed="${i===0?'true':'false'}" data-slide="${i}"></button>`).join('');
  const slides=$$('.hero-slide-bg',stage),dotEls=$$('.hero-dot',dots);
  function restartProgress(){
    if(!progress)return;
    progress.classList.remove('running');
    void progress.offsetWidth;
    if(!paused)progress.classList.add('running');
  }
  function animateContent(){
    content.classList.remove('slide-enter');
    void content.offsetWidth;
    content.classList.add('slide-enter');
  }
  function show(index,userAction=false){
    current=(index+items.length)%items.length;
    const m=items[current];
    slides.forEach((s,i)=>s.classList.toggle('active',i===current));
    dotEls.forEach((d,i)=>{d.classList.toggle('active',i===current);d.setAttribute('aria-pressed',i===current?'true':'false')});
    $('#heroTitle').textContent=m.title.toUpperCase();
    $('#heroEyebrow').textContent=`${m.tag} · Latest premiere`;
    $('#heroRating').textContent=`★ ${m.rating}`;
    $('#heroYear').textContent=m.year;
    $('#heroRuntime').textContent=m.runtime;
    $('#heroGenre').textContent=m.genre;
    $('#heroDescription').textContent=m.desc;
    $('#heroWatch').href=`watch.html?id=${m.id}`;
    $('#heroInfo').href=`movie.html?id=${m.id}`;
    $('#heroPoster').src=`assets/img/posters/${m.id}.svg`;
    $('#heroPoster').alt=`${m.title} poster`;
    $('#heroPreviewTitle').textContent=m.title;
    $('#heroPreviewMeta').textContent=`${m.genre} · ${m.year}`;
    $('#heroCount').textContent=`${String(current+1).padStart(2,'0')} / ${String(items.length).padStart(2,'0')}`;
    animateContent();
    restartProgress();
    if(userAction)restartTimer();
  }
  function next(){show(current+1)}
  function prev(){show(current-1)}
  function stopTimer(){if(timer){clearInterval(timer);timer=null}}
  function startTimer(){if(paused||document.hidden)return;stopTimer();timer=setInterval(next,6500);restartProgress()}
  function restartTimer(){stopTimer();startTimer()}
  $('#heroNext')?.addEventListener('click',()=>{show(current+1,true)});
  $('#heroPrev')?.addEventListener('click',()=>{show(current-1,true)});
  dotEls.forEach(d=>d.addEventListener('click',()=>show(Number(d.dataset.slide),true)));
  root.addEventListener('mouseenter',()=>{paused=true;stopTimer();progress?.classList.remove('running')});
  root.addEventListener('mouseleave',()=>{paused=false;startTimer()});
  root.addEventListener('focusin',()=>{paused=true;stopTimer();progress?.classList.remove('running')});
  root.addEventListener('focusout',e=>{if(!root.contains(e.relatedTarget)){paused=false;startTimer()}});
  root.addEventListener('keydown',e=>{if(e.key==='ArrowRight'){e.preventDefault();show(current+1,true)}if(e.key==='ArrowLeft'){e.preventDefault();show(current-1,true)}});
  root.addEventListener('touchstart',e=>{touchStartX=e.changedTouches[0].clientX},{passive:true});
  root.addEventListener('touchend',e=>{const dx=e.changedTouches[0].clientX-touchStartX;if(Math.abs(dx)>45)show(current+(dx<0?1:-1),true)},{passive:true});
  document.addEventListener('visibilitychange',()=>{if(document.hidden)stopTimer();else if(!paused)startTimer()});
  show(0);
  startTimer();
}
function initRevealEffects(){
  const reduce=window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const nodes=$$('.section .section-head,.movie-card,.feature-tile,.app-banner,.genre-card,.panel,.content-page .prose');
  if(reduce){nodes.forEach(n=>n.classList.add('is-visible'));return}
  nodes.forEach((n,i)=>{n.classList.add('reveal');n.style.setProperty('--reveal-delay',`${Math.min((i%6)*55,275)}ms`)});
  if(!('IntersectionObserver'in window)){nodes.forEach(n=>n.classList.add('is-visible'));return}
  const io=new IntersectionObserver(entries=>entries.forEach(e=>{if(e.isIntersecting){e.target.classList.add('is-visible');io.unobserve(e.target)}}),{threshold:.09,rootMargin:'0px 0px -30px'});
  nodes.forEach(n=>io.observe(n));
}
document.addEventListener('DOMContentLoaded',()=>{if(document.body.dataset.page==='home')initHeroSlider();requestAnimationFrame(initRevealEffects)});

// ===== V3: easier discovery, movie rails, quick search and dynamic SEO =====
function card(m,prefix=''){
  return `<article class="movie-card" data-title="${m.title.toLowerCase()}" data-genre="${m.genre}"><div class="poster-wrap"><a href="${prefix}movie.html?id=${m.id}" class="poster-link" aria-label="View ${m.title} details"><img loading="lazy" decoding="async" src="${prefix}assets/img/posters/${m.id}.svg" alt="${m.title} poster"></a><span class="badge">${m.tag}</span><span class="rating-pill">★ ${m.rating}</span><span class="poster-overlay"><span></span><span class="card-actions"><a class="card-action secondary" href="${prefix}movie.html?id=${m.id}" aria-label="More information about ${m.title}">i</a><a class="card-action primary" href="${prefix}watch.html?id=${m.id}" aria-label="Watch ${m.title} in Android app">${icon('play',20)}</a></span></span></div><div class="movie-info"><h3><a href="${prefix}movie.html?id=${m.id}">${m.title}</a></h3><div class="movie-sub"><span>${m.year} · ${m.genre}</span><span>${m.runtime}</span></div></div></article>`
}
function initHome(){
  renderMovies('#trending',MOVIES);
  renderMovies('#latest',[...MOVIES].sort((a,b)=>b.year-a.year||Number(b.rating)-Number(a.rating)));
  renderMovies('#recommended',MOVIES.slice(2).concat(MOVIES.slice(0,2)));
}
function initMovies(){
  renderMovies('#allMovies');
  const input=$('#movieSearch'),genre=$('#genreFilter'),year=$('#yearFilter');
  const initialGenre=qs('genre'); if(initialGenre&&genre)genre.value=initialGenre;
  function apply(){
    const q=(input?.value||'').toLowerCase(),g=genre?.value||'',y=year?.value||'';
    const arr=MOVIES.filter(m=>(!q||m.title.toLowerCase().includes(q)||m.genre.toLowerCase().includes(q))&&(!g||m.genre===g)&&(!y||String(m.year)===y));
    renderMovies('#allMovies',arr); if($('#resultsCount'))$('#resultsCount').textContent=`${arr.length} titles`;
    if(!arr.length&&$('#allMovies'))$('#allMovies').innerHTML='<div class="panel" style="grid-column:1/-1"><h3>No matches found</h3><p class="muted">Try a different title, genre or year.</p></div>';
  }
  input?.addEventListener('input',apply); genre?.addEventListener('change',apply); year?.addEventListener('change',apply); apply();
}
function durationISO(runtime){const h=runtime.match(/(\d+)h/)?.[1]||0,m=runtime.match(/(\d+)m/)?.[1]||0;return `PT${h}H${m}M`}
function setMeta(selector,attr,value){const el=$(selector);if(el)el.setAttribute(attr,value)}
function initMovie(){
  const m=movieById(qs('id'));
  const hero=$('#detailsHero'); if(hero)hero.style.backgroundImage=`url(assets/img/backdrops/${m.id}.svg)`;
  $$('[data-movie-title]').forEach(x=>x.textContent=m.title); $$('[data-movie-desc]').forEach(x=>x.textContent=m.desc); $$('[data-movie-year]').forEach(x=>x.textContent=m.year); $$('[data-movie-genre]').forEach(x=>x.textContent=m.genre); $$('[data-movie-runtime]').forEach(x=>x.textContent=m.runtime); $$('[data-movie-rating]').forEach(x=>x.textContent=m.rating);
  const p=$('#detailsPoster'); if(p){p.src=`assets/img/posters/${m.id}.svg`;p.alt=`${m.title} poster`} const w=$('#watchBtn'); if(w)w.href=`watch.html?id=${m.id}`;
  renderMovies('#relatedMovies',MOVIES.filter(x=>x.id!==m.id&&x.genre===m.genre).concat(MOVIES.filter(x=>x.id!==m.id&&x.genre!==m.genre)),6);
  const canonical=`https://cinemaone.example/en/movie/${m.id}`, title=`${m.title} (${m.year}) - Cinema One`, image=`https://cinemaone.example/assets/img/posters/${m.id}.png`;
  document.title=title; setMeta('meta[name="description"]','content',m.desc); setMeta('link[rel="canonical"]','href',canonical); setMeta('meta[property="og:title"]','content',title); setMeta('meta[property="og:description"]','content',m.desc); setMeta('meta[property="og:url"]','content',canonical); setMeta('meta[property="og:image"]','content',image); setMeta('meta[name="twitter:title"]','content',title); setMeta('meta[name="twitter:description"]','content',m.desc); setMeta('meta[name="twitter:image"]','content',image);
  const ms=$('#movieStructuredData'); if(ms)ms.textContent=JSON.stringify({'@context':'https://schema.org','@type':'Movie','name':m.title,'description':m.desc,'image':image,'dateCreated':`${m.year}-01-01`,'genre':m.genre,'duration':durationISO(m.runtime),'url':canonical});
  const bs=$('#breadcrumbStructuredData'); if(bs)bs.textContent=JSON.stringify({'@context':'https://schema.org','@type':'BreadcrumbList','itemListElement':[{'@type':'ListItem','position':1,'name':'Movies','item':'https://cinemaone.example/movies'},{'@type':'ListItem','position':2,'name':m.title,'item':canonical}]});
}
function decorateRails(){
  $$('.movie-rail').forEach((rail,ri)=>{
    const cards=$$('.movie-card',rail); if(rail.dataset.rail==='ranked')cards.slice(0,10).forEach((c,i)=>{c.classList.add('ranked-card');c.dataset.rank=i+1});
    const head=rail.closest('.container')?.querySelector('.section-head'); if(!head||head.querySelector('.rail-nav'))return;
    const link=head.querySelector('.link'); const actions=document.createElement('div'); actions.className='section-head-actions'; if(link)actions.appendChild(link);
    actions.insertAdjacentHTML('beforeend','<div class="rail-nav" aria-label="Carousel controls"><button class="rail-btn prev" type="button" aria-label="Scroll left">‹</button><button class="rail-btn next" type="button" aria-label="Scroll right">›</button></div>'); head.appendChild(actions);
    const prev=actions.querySelector('.prev'),next=actions.querySelector('.next'); const step=()=>Math.max(260,rail.clientWidth*.78);
    prev.addEventListener('click',()=>rail.scrollBy({left:-step(),behavior:'smooth'})); next.addEventListener('click',()=>rail.scrollBy({left:step(),behavior:'smooth'}));
    const update=()=>{prev.disabled=rail.scrollLeft<=3;next.disabled=rail.scrollLeft+rail.clientWidth>=rail.scrollWidth-3}; rail.addEventListener('scroll',update,{passive:true}); window.addEventListener('resize',update); update();
  })
}
function quickResult(m){return `<a class="quick-result" href="movie.html?id=${m.id}"><img src="assets/img/posters/${m.id}.svg" alt="" loading="lazy"><span><strong>${m.title}</strong><small>${m.year} · ${m.genre} · ${m.runtime}</small></span><span class="quick-result-score">★ ${m.rating}</span></a>`}
function initQuickSearch(){
  if(document.body.closest('.admin-shell')||location.pathname.includes('/admin/'))return;
  const wrap=document.createElement('div'); wrap.className='quick-search'; wrap.setAttribute('aria-hidden','true'); wrap.innerHTML=`<div class="quick-search-dialog" role="dialog" aria-modal="true" aria-label="Search movies"><div class="quick-search-head"><div class="quick-search-field">${icon('search',19)}<input id="quickSearchInput" type="search" placeholder="Search movies, genres..." autocomplete="off" aria-label="Search movies"><span class="search-key">ESC</span></div><button class="quick-search-close" type="button" aria-label="Close search">×</button></div><div class="quick-search-body" id="quickSearchResults" aria-live="polite"></div><div class="quick-search-foot"><span>Tip: press / anywhere to search</span><a href="search.html">Advanced search →</a></div></div>`; document.body.appendChild(wrap);
  const input=$('#quickSearchInput'),out=$('#quickSearchResults'); let lastFocus=null;
  function draw(){const q=input.value.trim().toLowerCase();const list=(q?MOVIES.filter(m=>`${m.title} ${m.genre} ${m.desc}`.toLowerCase().includes(q)):MOVIES.slice(0,6)).slice(0,7);out.innerHTML=list.length?list.map(quickResult).join(''):'<div class="quick-search-empty"><strong>No movies found</strong><br>Try another title or genre.</div>'}
  function open(){lastFocus=document.activeElement;wrap.classList.add('open');wrap.setAttribute('aria-hidden','false');document.body.classList.add('search-open');draw();setTimeout(()=>input.focus(),30)}
  function close(){wrap.classList.remove('open');wrap.setAttribute('aria-hidden','true');document.body.classList.remove('search-open');lastFocus?.focus?.()}
  input.addEventListener('input',draw); $('.quick-search-close',wrap).addEventListener('click',close); wrap.addEventListener('click',e=>{if(e.target===wrap)close()});
  $$('.header a[aria-label="Search"],.discover-search').forEach(a=>a.addEventListener('click',e=>{e.preventDefault();open()}));
  document.addEventListener('keydown',e=>{const typing=/INPUT|TEXTAREA|SELECT/.test(document.activeElement?.tagName||'');if(e.key==='/'&&!typing){e.preventDefault();open()}if(e.key==='Escape'&&wrap.classList.contains('open'))close()});
}
function enhanceHeroPriority(){const p=$('#heroPoster');if(p){p.loading='eager';p.fetchPriority='high';p.decoding='async'}}
document.addEventListener('DOMContentLoaded',()=>{decorateRails();initQuickSearch();enhanceHeroPriority()});
