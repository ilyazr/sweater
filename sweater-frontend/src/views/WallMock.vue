<template>
  <div class="mx-3 pl-4 my-3 pt-4 overflow-auto cards-holder">
    <b-card-group columns class="b">


      <b-card header-tag="header" footer-tag="footer">
        <template #header>
          <b-row class="d-flex justify-content-start">
            <b-col class="d-flex align-items-center">
              <b-avatar variant="info" src="https://placekitten.com/300/300"></b-avatar>
              <div class="ml-3 my-auto">
                <p class="font-weight-bold mb-0">Elon Musk</p>
                <p class="my-0 text-muted text-left">@elonmusk</p>
              </div>
            </b-col>
            <b-col></b-col>
          </b-row>
        </template>
        <b-card-title>
          Starship
        </b-card-title>
        <b-card-sub-title>
          #bla-bla
        </b-card-sub-title>
        <b-card-text>Header and footers using slots.</b-card-text>
        <b-button href="#" variant="primary">Go somewhere</b-button>
        <template #footer>
          <b-row class="d-flex justify-content-center">
            <b-col>
              <b-icon icon="chat"></b-icon>
            </b-col>
            <b-col>
              <b-icon icon="heart"></b-icon>
            </b-col>
            <b-col>
              <b-icon icon="share"></b-icon>
            </b-col>
            <b-col>
              <b-icon icon="three-dots"></b-icon>
            </b-col>
          </b-row>
        </template>
      </b-card>


      <b-card header-tag="header" footer-tag="footer"
              img-src="https://pbs.twimg.com/media/EvBNsOlUYAEzWjm?format=jpg&name=4096x4096"
              img-top >
        <template #header>
          <b-row class="d-flex justify-content-start">
            <b-col class="d-flex align-items-center">
              <b-avatar variant="info" src="https://placekitten.com/300/300"></b-avatar>
              <div class="ml-3 my-auto">
                <em>Elon Musk</em>
                <p class="my-0 text-muted">@elonmusk</p>
              </div>
            </b-col>
            <b-col></b-col>
          </b-row>
        </template>
        <b-card-title>
          Starship
        </b-card-title>
        <b-card-sub-title>
          #SpaceX
        </b-card-sub-title>
        <b-card-text>
          The
          @SpaceX
          Crew Dragon has officially been in space for more than 100 days!

          The Crew-1 mission launched on Nov. 15, 2020 & arrived at the
          @Space_Station
          on Nov. 16, carrying Shannon Walker,
          @AstroVicGlover
          ,
          @Astro_illini
          &
          @Astro_Soichi
          . They will return in late April/early May.
        </b-card-text>
        <b-button href="#" variant="primary">Go somewhere</b-button>
        <template #footer>
          <b-row class="d-flex justify-content-center">
            <b-col>
              <b-icon icon="chat"></b-icon>
            </b-col>
            <b-col>
              <b-icon icon="heart"></b-icon>
            </b-col>
            <b-col>
              <b-icon icon="share"></b-icon>
            </b-col>
            <b-col>
              <b-icon icon="three-dots"></b-icon>
            </b-col>
          </b-row>
        </template>
      </b-card>


      <b-card
          title="Card title that wraps to a new line"
          img-src="https://placekitten.com/g/400/450"
          img-alt="Image"
          img-top
      >
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This content is a little bit longer.
        </b-card-text>
      </b-card>

      <b-card header="Quote">
        <blockquote class="blockquote mb-0">
          <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
          <footer class="blockquote-footer">
            Someone famous in <cite title="Source Title">Source Title</cite>
          </footer>
        </blockquote>
      </b-card>

      <b-card title="Title" img-src="https://placekitten.com/500/350" img-alt="Image" img-top>
        <b-card-text>
          This card has supporting text below as a natural lead-in to additional content.
        </b-card-text>
        <b-card-text class="small text-muted">Last updated 3 mins ago</b-card-text>
      </b-card>

      <b-card bg-variant="primary" text-variant="white">
        <blockquote class="card-blockquote">
          <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
          <footer>
            <small>Someone famous in <cite title="Source Title">Source Title</cite></small>
          </footer>
        </blockquote>
      </b-card>

      <b-card>
        <b-card-title>Title</b-card-title>
        <b-card-text>
          This card has supporting text below as a natural lead-in to additional content.
        </b-card-text>
        <b-card-text class="small text-muted">Last updated 3 mins ago</b-card-text>
      </b-card>

      <b-card img-src="https://picsum.photos/400/400/?image=41" img-alt="Image" overlay></b-card>

      <b-card img-src="https://picsum.photos/400/200/?image=41" img-alt="Image" img-top>
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This card has even longer content than the first.
        </b-card-text>
        <template #footer>
          <small class="text-muted">Footer Text</small>
        </template>
      </b-card>

      <b-card img-src="https://picsum.photos/400/200/?image=41" img-alt="Image" img-top>
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This card has even longer content than the first.
        </b-card-text>
        <template #footer>
          <small class="text-muted">Footer Text</small>
        </template>
      </b-card>

      <b-card img-src="https://picsum.photos/400/200/?image=41" img-alt="Image" img-top>
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This card has even longer content than the first.
        </b-card-text>
        <template #footer>
          <small class="text-muted">Footer Text</small>
        </template>
      </b-card>


      <b-card
          title="Card title that wraps to a new line"
          img-src="https://placekitten.com/g/400/450"
          img-alt="Image"
          img-top
      >
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This content is a little bit longer.
        </b-card-text>
      </b-card>

      <b-card header="Quote">
        <blockquote class="blockquote mb-0">
          <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
          <footer class="blockquote-footer">
            Someone famous in <cite title="Source Title">Source Title</cite>
          </footer>
        </blockquote>
      </b-card>

      <b-card title="Title" img-src="https://placekitten.com/500/350" img-alt="Image" img-top>
        <b-card-text>
          This card has supporting text below as a natural lead-in to additional content.
        </b-card-text>
        <b-card-text class="small text-muted">Last updated 3 mins ago</b-card-text>
      </b-card>

      <b-card bg-variant="primary" text-variant="white">
        <blockquote class="card-blockquote">
          <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
          <footer>
            <small>Someone famous in <cite title="Source Title">Source Title</cite></small>
          </footer>
        </blockquote>
      </b-card>

      <b-card>
        <b-card-title>Title</b-card-title>
        <b-card-text>
          This card has supporting text below as a natural lead-in to additional content.
        </b-card-text>
        <b-card-text class="small text-muted">Last updated 3 mins ago</b-card-text>
      </b-card>

      <b-card img-src="https://picsum.photos/400/400/?image=41" img-alt="Image" overlay></b-card>

      <b-card img-src="https://picsum.photos/400/200/?image=41" img-alt="Image" img-top>
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This card has even longer content than the first.
        </b-card-text>
        <template #footer>
          <small class="text-muted">Footer Text</small>
        </template>
      </b-card>

      <b-card img-src="https://picsum.photos/400/200/?image=41" img-alt="Image" img-top>
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This card has even longer content than the first.
        </b-card-text>
        <template #footer>
          <small class="text-muted">Footer Text</small>
        </template>
      </b-card>

      <b-card img-src="https://picsum.photos/400/200/?image=41" img-alt="Image" img-top>
        <b-card-text>
          This is a wider card with supporting text below as a natural lead-in to additional content.
          This card has even longer content than the first.
        </b-card-text>
        <template #footer>
          <small class="text-muted">Footer Text</small>
        </template>
      </b-card>
    </b-card-group>
  </div>
</template>

<script>
export default {
name: "WallMock",
}
</script>

<style scoped>
  .cards-holder {
    height: 100vh;
    padding-right: 55px;
  }
  .card {
    background-color: #2C2A38;
    color: whitesmoke;
    border-radius: 15px !important;
  }
  .card-header {
    border-bottom: none;
  }
  .card-footer {
    border-top: none;
  }
</style>